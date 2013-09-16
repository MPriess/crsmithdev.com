---
title: Running Redis 2.6 on Docker
tags: docker redis ubuntu
---

## Running Redis 2.6 on Docker

### Docker

With the end of my free tier eligibility looming on AWS, I took advantage of the [Rackspace developer discount](http://developer.rackspace.com/devtrial/) and set up a new account and personal server this weekend.  One of the technologies I've been most interested in recently is [Docker](http://www.docker.io), a container engine for Linux which aims to be the intermodal shipping container for applications and their dependencies.  A brand-new box seemed like the perfect time to dig in to it.

To get a sense of what Docker is and how it works, I'd recommend going through the [getting started](http://www.docker.io/gettingstarted/) tutorial as well as some of the examples in the [documentation](http://docs.docker.io/en/latest/).  However, here's a very brief rundown:

- Docker uses [LXC](https://en.wikipedia.org/wiki/LXC) and a [union file system](https://en.wikipedia.org/wiki/Union_filesystem) ([AUFS](https://en.wikipedia.org/wiki/Aufs)) to run isolated containers (**not** VMs).
- Software and its dependencies are packaged into an **image**.
- Images are immutable and stateless.
- Images can be committed in layers to form more complex images.
- An image running a process is called a **container**, which is stateful.
- A container exists as running or stopped, and can be run interactively or in the background.
- Images are lightweight, perhaps 100Mb for a Redis server running on Ubuntu.
- Containers are not virtual machines, so they are lightening-fast to boot and lightweight on resources.

One of the documentation examples describes setting up a [Redis](http://redis.io) service.  Following the example was straightforward, but I felt it was missing two things when I was finished.  First, it uses Redis 2.4, which is already quite out of date (as of this writing, Redis 2.8 is nearing release).  And, there are a few more interesting options Docker provides that weren't used.

### Installing Redis 2.6

The first thing to do is start a container from a base image, in this case the `ubuntu` image pulled during setup:

    sudo docker run -i -t ubuntu /bin/bash

This results in a root shell on a new, running Ubuntu container.  A few things will be needed in order to download, build and test Redis:

    apt-get install wget build-essential tcl8.5

To install Redis 2.6, it's necessary to build it from source: 

    wget get http://download.redis.io/releases/redis-2.6.16.tar.gz
    tar xvf redis-2.6.16.tar.gz
    cd redis-2.6.16

Build it, run some tests and install once completed:

    make
    make test
    make install

Lastly, move the config file to a more standard location:

    mkdir /etc/redis
    mv redis.conf /etc/redis/redis.conf

Verify the server is working by running the following:

    redis-server /etc/redis/redis.conf


### Run commands and images 

In the example, the resulting container is committed to an image, and then run:

    sudo docker commit <container_id> crsmithdev/redis
    sudo docker run -d -p 6379 crsmithdev/redis2.6 /usr/bin/redis-server /etc/redis/redis.conf

This is still a bit clunky &mdash; why do `redis-server` and the config file have to be specified each time it's run?  Forunately, they can be built into the image itself:

    sudo docker commit -run='{"Cmd":["/usr/local/bin/redis-server", "/etc/redis/redis.conf"]}' \
        <container_id> crsmithdev/redis2.6

That way, you can run the container like so:

    sudo docker run -d -p 6379:6379 crsmithdev/redis2.6

Specifying -p 6379:6379 ensures that the server's port 6379 is mapped to the container's port 6379.  Otherwise, Docker will assign a random local server port in the 49000s, which is probably unwanted in most non-development environments.

Note that it is still possible to override the image-specified run command.  The following will open a shell using the image, instead of launching Redis:

    sudo docker run -i -t crsmithdev/redis2.6 /bin/bash

### Handling data

One important point:  what about the data and log files that result from the Redis process?  Every time I run Redis from that image, I get a new container with fresh data.  That's ideal for some situations, but less so for for others: in a production environment, it's entirely possible I'd want to be able to start a new Redis container, but be able to load a dumpfile from a previous one.

Fortunately, you can share one or more volumes with the host server easily.  Modify `redis.conf` on the container to specify a dumpfile location of your choice:

    dir /data/redis

Then, run the image specifying a mount point on the server:

    sudo docker run -d -p 6379:6379 -v /mnt/redis:/data/redis:rw crsmithdev/redis2.6

Connecting via `redis-cli` and executing `SAVE` should result in a `dump.rdb` file in `/mnt/redis`.  Redis will be logging to stdout unless specified otherwise, so the logs are viewable using a Docker command:

    sudo docker logs <container_d>

If you specify a different logfile location in `redis.conf`, it's possible to add a second volume to the `run` command.

### Fin!

And that's it.  This image can then be downloaded and started, producing a running, fully-functional Redis server in literally a few seconds.

You can grab my image [here](https://index.docker.io/u/crsmithdev/redis2.6/).
