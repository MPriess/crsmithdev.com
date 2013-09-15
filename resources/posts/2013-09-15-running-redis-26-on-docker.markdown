---
title: Running Redis 2.6 on Docker
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

One of the documentation examples describes setting up a [Redis](http://redis.io) service.  Following the example was straightforward, but I felt it was missing two things when I was finished.  First, it uses Redis 2.4, which is already quite out of date (as of this writing, Redis 2.8 is nearing release).  And second, it still required some parameters passed in order to actually launch the container, including the `redis-server` path and port, which seemed unnecessary.  Fortunately, I found it was easy to address both of these, and was a great introduction to using Docker. 


### Installing Redis 2.6

The first thing to do is start a container from a base image, in this case the `ubuntu` image pulled during setup:

    docker run -i -t ubuntu /bin/bash

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


### Adding run arguments to the image

In the example, the resulting container is commited to an image and run like so:

    sudo docker commit <container_id> crsmithdev/redis
    sudo docker run -d -p 6379 crsmithdev/redis /usr/bin/redis-server

However, it makes more sense to me that it **always** runs on port 6379, and `redis-server` is the only command this container should ever be running.  Here's an alternate way to commit the container that addresses both these issues:

    sudo docker commit -run='{"Cmd":["/usr/local/bin/redis-server", "/etc/redis/redis.conf"] \
        "PortSpecs":["6379"]}' <container_id> crsmithdev/redis2.6

With that, you can run the container with no arguments:

    sudo docker run -d crsmithdev/redis2.6

Then, connect with `redis-cli` and verify that it's running correctly.

### Fin!

And that's it.  This image can then be downloaded and started, producing a running, fully-functional Redis server in literally a few seconds.

You can grab my image [here](https://index.docker.io/u/crsmithdev/redis2.6/).
