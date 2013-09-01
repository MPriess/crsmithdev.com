---
title: A refresh of crsmithdev.com with Bootstrap and Static
---

## A refresh of crsmithdev.com with Bootstrap 3 and Static

### Previously

Earlier this year, I finally set up a blog on my domain, having owned but left it unused for over a year.  My needs were simple:  it was to be a completely static site, hostable on GitHub Pages or Dropbox, and the focus of the project was in **no** way to be the technology or process of creating and maintaining it.  Despite the part of me that automatically geeked out at the opportunity to build my own completely custom blog generator from scratch, the point of doing it was to provide myself with a straightforward platform for *writing*, not to go on a technical adventure in creating one.  Although I've only written two posts on it so far, the effort was successful: in about 2 hours, I'd set up [Octopress](http://octopress.org) and had it deploying to Pages.  The result looked like this:

<img class="img-responsive img-thumbnail blog-image" src="/img/crsmithdev_com_old.jpg"/>

I found it usable but lacking in a few key ways, the most significant of which was that I was simply underwhlemed with the themes available for Octopress, and had little interest in building or heavily-modifying an Octopress theme.  Moreover, it felt very much like a monolithic framework, with bits of boilerplate code strewn about my site repo.  What I realized is that I really wanted a simple engine that would handle the work of converting Markdown to HTML with templates, but would otherwise stay out of the way as much as possible, impose little structure on the site, and most importantly, would leave me total control in designing what the output looked like.

I was also eager to address a few specific issues:

- It was *only* a blog, lacking even a bio page.
- Responsiveness was questionable.
- Syntax highlighting was not supported.
- I wanted to add a simple display of recent GitHub activity.

Lastly, as Clojure is quickly eclipsing all others as my hacking language of choice, I very much wanted to see what the ecosystem might offer in terms of static site generation tools.

### Components

In the end, I selected the following:

- [Bootstrap 3](http://getbootstrap.com) - Newly released, rebuilt and responsive-first.
- [Flatly](http://bootswatch.com/flatly/) theme from [Bootswatch](http://bootswatch.com/) - a flat, simple and readable theme for Bootstrap 3.
- [Static](https://github.com/nakkaya/static) - A tiny, embeddable static site generator in Clojure.
- [Font Awesome](http://fortawesome.github.io/Font-Awesome/) - high-quality icons, here specifically for social media networks. 
- [google-code-prettify](https://code.google.com/p/google-code-prettify/) - code syntax highlighting.

### Static

Static is a very simple static site generator, with full documentation that spans about [two pages](http:/nakkaya.com/static.html).  What's most interesting about Static (compared to Octropress, at least) is that it is built as a separate project, and then the .jar is copied into the repo for the site that will use it.  This means that the only traces of it that end up in the project are a few conventions regarding directory structure.

Here's all that's needed to get started with Static:

<!--?prettify lang=sh-->

    git clone https://github.com/nakkaya/static.git
    cd static
    lein deps
    lein uberjar

This results in a .jar named `static-app.jar` in the `target` directory, which can then be copied into a fresh repo for a site:

<!--?prettify lang=sh-->

    cd ..
    mkdir crsmithdev.com
    cd crsmithdev.com
    git init
    cp ../static/target/static-app.jar .
	
Here's the minimum directory structure and files necessary to get started:

    .
    |-- config.clj
    `-- resources
        |-- posts
        |-- public
        |-- site
        |-- templates
            `-- default.clj

A brief description of what all these are:

- `config.clj` - global site configuration options.
- `posts` - blog posts, in markdown or org-mode format.
- `public` - public site resources and directories (`js`, `css`, etc.), to be copied to the root of the generated site.
- `site` - Hiccup templates for the content of non-blog-post pages.
- `templates` - Hiccup templates for all pages.

All that's needed to build the site is this:

<!--?prettify lang=sh-->

    java -jar static-app.jar --build

The `--watch` option can be used to rebuild automatically when a file changes.  When the site builds, the output should look like this:


    [+] INFO: Using tmp location: /var/folders/r5/30xb2fj573b_s9_2f18y4s_00000gn/T/static/
    [+] INFO: Processing Public  0.011 secs
    [+] INFO: Processing Site  0.213 secs
    [+] INFO: Processing Posts  0.695 secs
    [+] INFO: Creating RSS  0.07 secs
    [+] INFO: Creating Tags  0.03 secs
    [+] INFO: Creating Sitemap  0.0040 secs
    [+] INFO: Creating Aliases  0.01 secs
    [+] INFO: Build took  1.034 secs

An `html` directory will be created in the root of the site, containing all the generated HTML.  I found that pointing my local nginx at this folder was the most straightforward way to serve the site locally while working on it, although Static does offer a `--jetty` option to serve it as well.

The contents of my config.clj are as follows (note that it is possible to modify the expected directory structure here as well):

<!--?prettify lang=clj-->

    [:site-title "crsmithdev.com"
     :site-description "crsmithdev.com"
     :site-url "http://crsmithdev.com"
     :in-dir "resources/"
     :out-dir "html/"
     :default-template "default.clj"
     :encoding "UTF-8"
     :blog-as-index false
     :create-archives false
     :atomic-build true]

Blog posts require only a simple header with some metadata, as shown below:

    ---
    title: Building Better Email Habits with Mailbox
    ---

Static's [documentation](http://nakkaya.com/static.html) lists a few more options here, including the specification of tags, an alias, a non-default template, and more.

### HTML templating with Hiccup

Static uses [Hiccup](https://github.com/weavejester/hiccup), a great templating library for Clojure, to specify the structure of pages it generates.  Having never used it before, I instantly found it to be very natural and efficient &mdash; the sytax is extremely minimal, vectors and maps are used for element and their attributes, respectively, and it is very easy to embed other Clojure code amongst element definitions.

Here's what the first few lines of my default template look like:

<!--?prettify lang=clj-->

    [:html
     {:xmlns "http://www.w3.org/1999/xhtml" :lang "en" :xml:lang "en"}
     [:head
      [:meta {:http-equiv "content-type" :content "text/html; charset=UTF-8"}]
      [:meta {:name "description" :content (:description metadata)}]
      [:meta {:name "keywords" :content (:tags metadata)}]
      [:meta {:name "author" :content "Chris Smith"}]
      [:meta {:name "viewport" :content "width=device-width, initial-scale=1.0"}]
      [:link {:rel "icon" :href "/images/favicon.ico" :type "image/x-icon"}]
      [:link {:rel "shortcut icon" :href "/images/favicon.ico" :type "image/x-icon"}]

Note the access of the `:description` and `:tags` from `metadata`.  Static injects a few values into page rendering, specifically `metadata` and `content`.  `metadata` provides some information about what kind of page is being rendered, while `content` is the actual Markdown or Hiccup-generated content that the template will include.  Because of this, it is possible to specify different behaviors depending on what is being rendered:

<!--?prettify lang=clj-->

	[:div.content
	 [:div.container
	  (if (= (:type metadata) :post)
		[:div.row
		 [:div.col-md-12
		  content
		  [:div#disqus_thread]
		  [:script {:type "text/javascript"}
		   "// ... (disqus js)"]]]
		content)

Above, a simple Bootstrap grid is created if the content of the page is a post, and then followed by the standard JS to include Disqus comments on the page.  Note the terse syntax for specifying element classes:  this is actually one of two possible syntaxes to define classes and ids.  Below, these two forms are equivalent:

<!--?prettify lang=clj-->

	[:div {:class "col-md-12"} "..."]
	[:div.col-md-12 "..."]

### Bootstrap 3, Font Awesome, and theming

Fortunately, Bootstrap 3 was nearing release as I was beginning to work on the site, so I grabbed the RC2 version and went to work.  [Bootswatch](http://bootswatch.com/) provides a nice selection of attractive, free themes for Bootstrap 3, of which I picked [Flatly](http://bootswatch.com/flatly/). Although Bootstrap does include the free version of [Glyphicons](http://glyphicons.com/), I've used [Font Awesome](http://fortawesome.github.io/Font-Awesome/) extensively both a work and personally, and find the selection both much larger and of higher quality.  It has built-in, high-quality icons for Twitter, GitHub and LinkedIn (amongst many, many others), making it an easy choice here.

There are plenty of great starting points / tutorials already out there for Bootstrap (I'd recommend this [starter template](http://getbootstrap.com/getting-started/#template)), and the use of it here is quite simple.  However, I did make a few notable alterations to the Flatly theme, with the goal of making the site a bit easier on the reader's eyes and more suitable for text-dense pages:

- Changed the standard font to **Source Sans Pro** (from the default **Lato**), both of which are available from [Google Fonts](http://www.google.com/fonts).  Source Sans Pro is a bit lighter and wider, and feels easier to read.
- Changed the code font to **Source Code Pro** (from the default **Monaco**).  Source Code Pro is my favorite fixed-width font, I use it as the font in all my editors and find it to be extremely readable.
- Bumped the font size to 1.7em.
- Increased line-height to 28px.
- Narrowed the container max-width to 960px.

I found these resulted in a much more pleasant reading experience.

### Github Activity

While there are some JS libraries to access the GitHub API, my needs were so simple that I was unwilling to introduce additional dependencies to just perform one AJAX call and parse a little bit of JSON.  Note that while GitHub does provide public, un-authenticated access to developer activity, clients are rate limited to 60 requests per IP per hour.

The full code to retrieve, process and display my GitHub commits can be found [here](https://github.com/crsmithdev/crsmithdev.github.com/blob/master/js/crsmithdev.js), but here are some highlights.  I needed a function to retrieve some JSON from GitHub, transform that data into a list of DOM elements, and then apply those elements to any containers matching a certain CSS selector.  And of course, it's important to limit the number of commits that are displayed:

<!--?prettify lang=js-->

    var activity = function(sel, n) {
        var containers = $(sel);

        if (containers.length > 0) {
            $.ajax({
                url: 'https://api.github.com/users/crsmithdev/events',
                dataType: 'jsonp',
                success: function (json) {
                    var elements = commits(json.data, n);
                    containers.append(elements);
                }
            });
        }
    };

For actually generating elements from JSON, I could have used libraries like [underscore.js](http://underscorejs.org) and [moment.js](http://momentjs.org) to handle things like iteration, templating and date formatting, and I normally would have these in scope in a larger project.  Since I'm without them here, though, everything is done in vanilla JS. Parsing the JSON is straightforward, as every event that involves a commit will have a `payload.commit` property containing an array of commmits.  Using arrays and a native `.join()` function should be preferred to string concatenation, in the absence of templating:

<!--?prettify lang=js-->

    var repo = event.repo.name.split('/')[1];
    var date = toDateString(event.created_at);

    for (var j = 0; j < event.payload.commits.length; ++j) {
        var commit = event.payload.commits[j];

        var arr = ['<div><div><a href=https://github.com/"', event.repo.name, '/commit/',
            commit.sha, '">', commit.message, '</a> <span class="text-muted">', repo,
            '</span></div>', '<div>', date,	'</div></div>'];

        elements.push($(arr.join('')));
    }

Dates are handled with a simple function and an array of month names.  The GitHub API provides dates in ISO-8601 format (YYYY-MM-DDThh:mm:ssZ), so it's easy to extract the year, month, and day:

<!--?prettify lang=js-->

    var months = ['January', 'Febuary', 'March', 'April', 'May', 'June', 'July', 'August',
	    'September', 'October', 'November', 'December'];

    // ...

    var toDateString = function(date) {

        try {
            var parts = date.split('T')[0].split('-');
            var month = months[parseInt(parts[1]) - 1];
            return [parts[2], month, parts[0]].join(' ');
        }
        catch (e) {
            return '???';
        }
    };

And of course, all this is wrapped in a module that exposes only one public method, and run when ready:

<!--?prettify lang=js-->

    $(function() {
        ghActivity.activity('.gh-recent', 5);
    });

### Syntax Highlighting

Originally I attemped to use [highlight.js](http://softwaremaniacs.org/soft/highlight/en/), but quickly found it to be problematic:  nearly all of the guesses it made about what kind of syntax was being presented were wrong, and it was difficult to override its default guessing behavior, especially given that I was writing the posts in Markdown, not raw HTML.  Fortunately, [google-code-prettify](https://code.google.com/p/google-code-prettify/) was a much more usable option, even though it does require an [extension](https://code.google.com/p/google-code-prettify/source/browse/trunk/src/lang-clj.js) to handle Clojure.

If I *were* writing HTML, using google-code-prettify would look something like this:

<!--?prettify lang=html-->

    <pre class="prettyprint lang-clj"><code>
       [:h3 "Interests & Areas of Expertise"]
        [:ul
         [:li "API design, development and scalability"]
         [:li "Distributed systems and architecture"]
         [:li "Functional programming"]
         ; ...
    </code></pre>

But since I want to do this in Markdown, the situation is different.  There's no way to add a class to the auto-generated `<pre><code>...</code></pre>` blocks, and although I could use literal HTML instead, that brings with it some other issues (angle brackets in code then have to be manually escaped, for example).  Fortunately, google-code-prettify allows the use of directives in HTML comments preceding code blocks, meaning I can just write this in my posts:

    <!--?prettify lang=clj-->

        [:h3 "Interests & Areas of Expertise"]
         [:ul
          [:li "API design, development and scalability"]
          [:li "Distributed systems and architecture"]
          [:li "Functional programming"]
          ; ...


### Deployment

Deployment to GitHub Pages was very straightforward.  I nuked my existing [crsmithdev.github.com](https://github.com/crsmithdev/crsmithdev.github.com) master and copied over all the files from the `html` directory, being sure to add a CNAME file referencing my [crsmithdev.com](http://crsmithdev.com) domain so Pages would work currently under it.  One push and the site was up and running.

### Future work

I'm much happier with the site now, but still see some areas for improvement:

- Some optimizations could definitely improve load times.  I'll likely write a future post about this.
- I'd very much like to be able to partially render blog posts on the index page (a title and two paragraphs or so).
- Simply put, I need to write more.

Of course, the last of these is the most difficult for me, which is often a sign that it is the most important.

