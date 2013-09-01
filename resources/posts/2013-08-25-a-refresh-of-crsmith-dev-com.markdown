---
title: A refresh of crsmithdev.com with Bootstrap and Static
tags: tags
---

## A refresh of crsmithdev.com with Bootstrap 3 and Static

### Previously

Earlier this year, I finally put a blog up on my domain, having owned it but left it unused for over a year.  I had the simplest possible needs:  it was to be a completely static site, hostable on Github Pages or Dropbox, and the focus of the project was in **no** way to be the technology or process of creating and maintaining the site.  Despite the part of me that automatically geeked out at the opportunity to build my own completely custom static blog generator, the point of the project was to provide myself with a straightforward platform for writing, not to go on a technical adventure in creating one.  Although so far it has only resulted in two posts, including this one, I was successful in putting together something usable, and quickly:  in about 2 hours, I'd set up [Octopress](http://octopress.org) and had it deplying to Pages.  It ended up looking like this:

[image]

Not bad, but lacking in a few big ways.  The biggest was that I was underwhelmed with the themes available for Octopress, and still felt that a lot of the codebase of the site ended up being framework bits and boilerplate code from Octopress.  Quickly I realized that what I really wanted was an engine that would handle the conversion of Markdown to HTML with templates, but would otherwise stay out of the way and impose as little structure as possible.

I was also eager to add a few more dimensions to the site.  A bio / about page was clearly needed, as was a project page to highlight some of the things I've worked on.  A responsive-first design is now a fundamental requirement for web development, and I needed a clearer yet tasteful advertisement of my social media presences.  Finally, as I've been significantly more active on GitHub recently, I thought an interesting feature to add might be a display of recent commits.

Lastly, as Clojure is quickly eclipsing all others as my hacking language of choice, I very much wanted to see what the ecosystem might offer in terms of static site generation.

### Components

In the end, I selected the following:

- [Bootstrap 3](http://getbootstrap.com) - No better way to try it out, plus a responsive-first focus in 3.0
- [Flatly](http://bootswatch.com/flatly/) theme from [Bootswatch](http://bootswatch.com/) - flat, simple and readable theme for Bootstrap.
- [Static](https://github.com/nakkaya/static) - A tiny, embeddable static site generator in Clojure
- [Font Awesome](http://fortawesome.github.io/Font-Awesome/) - Slick social media icons.
- [highlight.js](http://softwaremaniacs.org/soft/highlight/en/) - Customizable syntax highlighting.

Github has an [API](http://developer.github.com/) that allows unauthenticated access to developer activity, so there's no need for an account with them, and aside from the requisite jQuery for Bootstrap, there shouldn't really be a need to pull in any other JS libraries.

### Static

Static is a very simple static site generator, with full documentation that spans about [two pages](http:/nakkaya.com/static.html).  What's most interesting about Static (compared to Octropress, at least) is that it is built as a separate project, and then the .jar is copied into the repo for the site that will use it.  This means that the only traces of it that end up in the project are a few conventions regarding directory structure.

Here's all that's needed to get started with Static:

<!--?prettify lang=sh-->

    git clone https://github.com/nakkaya/static.git
    cd static
    lein deps
    lein uberjar

This results with a .jar named `static-app.jar` in the `target` directory, which can then be moved into a fresh site repo:

<!--?prettify lang=sh-->

    mkdir crsmithdev.com
    git init
    cp ../static/target/static-app.jar .
	
Here's the minimum directory structure and files necessary to start building a site:

    .
    |-- config.clj
    `-- resources
        |-- posts
        |-- public
        |-- site
        |-- templates
            `-- default.clj

A brief description of what all these are:

- `config.clj` contains global site configuration options
- **posts** contains the actual blog post files, in markdown or org-mode format.
- **public** will have its directory contents copied to the root of the generated site, so this is where css, js directories should be placed.
- **site** should contain templates (Hiccup, here) for fragments of non-blog-post pages of the site.  In my case, this means some files named index.clj, about.clj, etc.
- **templates** should contain templates for rendering both posts and other pages in the site.

All that's needed to build the site is this:

<!--?prettify lang=sh-->

    java -jar static-app.jar --build

Or, to rebuild automatically when something is changed:

<!--?prettify lang=sh-->

    java -jar static-app.jar --watch

This will result in an 'html' directory appearing in the root of the site, containing the rendered HTML pages.  I found pointing my local nginx at this folder to be the best way to serve the site locally, although Static does offer a `--jetty` option to run it as well.

Blog post configuration is extremely simple, and should be familiar to anyone who has used Markdown for blogging.  Each post will simple require a short header with a few configuration values, as shown here:

    ---
    title: Building Better Email Habits with Mailbox
    ---

Static's [documentation](http://nakkaya.com/static.html) lists a few more options for the post header, allowing the specification of tags, an alias, and use of non-default templates.

### HTML templating with Hiccup

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

Note the access of the `:description` and `:tags` from `metadata`.

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

Here, some extra structure for Bootstrap, and the embedded code for Disqus, are injected in the page, but only if its `metadata` `:type` indicates that it is a post.  Also, note the terse syntax for Hiccup:  this is actually a 'short' form of ID / class specification it offers, which makes these two forms equivalent:

<!--?prettify lang=clj-->

	[:div {:class "col-md-12"} "..."]
	[:div.col-md-12 "..."]

### Bootstrap 3, Font Awesome, and theming

Fortunately, Bootstrap 3 was nearing release as I was beginning to work on the site, so I grabbed the RC2 version and went to work.  [Bootswatch](http://bootswatch.com/) provides a nice selection of attractive, free themes for Bootstrap 3, of which I picked [Flatly](http://bootswatch.com/flatly/).  As there are plenty of examples / tutorials on how to set up basic layouts with Bootstrap there's little need for one here.

Although Bootstrap does include the free version of [Glyphicons](http://glyphicons.com/), I've used [Font Awesome](http://fortawesome.github.io/Font-Awesome/) extensively both a work and personally, and find the selection both much larger and of higher quality.  It has built-in, high-quality icons for Twitter, GitHub and LinkedIn (amongst many, many others), making it an easy choice here.

### Github Activity

While there are some JS libraries for the GitHub API, my needs were quite simple here and I was unwilling to introduce more dependencies for a simple, static site.  As there's no need to register with GitHub to use the part of the API I was needing, it should be possible to retrieve, parse and display a list of recent activity using a single `$.ajax` request and some vanilla JavaScript.

The full code to retrieve, process and display my GitHub commits can be found [here](https://github.com/crsmithdev/crsmithdev.github.com/blob/master/js/crsmithdev.js), but here are some highlights.  I'll need a function to retrieve some JSON from GitHub, transform that data into a list of DOM elements, and then apply those elements to any containers matching a certain CSS selector.  And of course, I'll want to limit the number of commits that are displayed:

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

For actually generating elements from JSON, I could have used libraries like [underscore.js](http://underscorejs.org) and [moment.js](http://momentjs.org) to handle things like iteration, templating and date formatting, and I normally would have these in scope in a larger project.  Here, though, I see little reason to involve two more libraries for a few simple operations.  Parsing the JSON is straightforward, as every event that involves a commit will have a `payload.commit` property containing an array of commmits.  Using arrays and a native `.join()` function should be preferred to string concatenation, in the absence of templating:

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

Dates are handled with a simple function and an array of month names.  The GitHub API provides dates in ISO 8601 format (YYYY-MM-DDThh:mm:ssZ):

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

Finally, some quality syntax highlighting was needed.  I selected [google-code-prettify](https://code.google.com/p/google-code-prettify/).  All it requires to highlight syntax are a few classes:

<!--?prettify lang=html-->

    <pre class="prettyprint lang-clj"><code>
       [:h3 "Interests & Areas of Expertise"]
        [:ul
         [:li "API design, development and scalability"]
         [:li "Distributed systems and architecture"]
         [:li "Functional programming"]
         ; ...
    </code></pre>

Unfortunately, as posts are being written in Markdown, I can't add those classes to the automatically generated code blocks, and although I could use literal HTML in Markdown to manually define everything, then I would have to worry about angle brackets and other characters that would have to be escaped.  Fortunately, google-code-prettify allows the use of directives in comments to hint at what should be prettified, and with what language.  This means I can write the Markdown to produce the above like so:

    <!--?prettify lang=clj-->

        [:h3 "Interests & Areas of Expertise"]
         [:ul
          [:li "API design, development and scalability"]
          [:li "Distributed systems and architecture"]
          [:li "Functional programming"]
          ; ...

Note that a separate download is required for Clojure support, as it is provided by an extension.

### Deployment

Deployment is extremely straightforward.  I cleared out my existing [crsmithdev.github.com](https://github.com/crsmithdev/crsmithdev.github.com), and copied over all the files from the `html` directory, being sure to add a CNAME file referencing my [crsmithdev.com](http://crsmithdev.com) domain so Pages will work correctly under it.  One push later and all was up and running.

### Future work

I'm much happier with the look and feature set of the site now

- I think the load times could definitely be improved, especially considering this is a static site.  I'll likely have a future post on optimization.
- I need to take a look at how highlight.js both identifies various types of languages, as well as adjust the colors it uses.
- Put simply, I just need to write more!

Of course, the last is the most difficult of the three for me...which also means it is the most important.

