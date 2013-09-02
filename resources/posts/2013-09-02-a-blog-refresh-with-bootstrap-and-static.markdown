---
title: A Blog Refresh with Bootstrap and Static
---

## A Blog Refresh with Bootstrap 3 and Static

### Previously

Earlier this year, I finally set up a blog on my domain, having owned but left it unused for over a year.  My needs were simple:  it was to be a completely static site, hostable on GitHub Pages or Dropbox, and the focus of the project was in **no** way to be the technology or process of creating and maintaining it.  Despite the part of me that automatically geeked out at the opportunity to build my own completely custom blog generator from scratch, the point of doing it was to provide myself with a straightforward platform for *writing*, not to go on a technical adventure in creating one.  Although I've only written two posts on it so far, the effort was successful: in short order, I'd set up [Octopress](http://octopress.org) and had it deploying to Pages.  The result looked like this:

<img class="img-responsive img-thumbnail blog-image" src="/img/crsmithdev_com_old.jpg"/>

I found it usable but lacking in a few key ways, the most significant of which was that I was simply underwhelmed with the themes available for Octopress, and had little interest in building a new theme or heavily modifying an existing one.  Moreover, it felt very much like a monolithic framework, into a tiny corner of which were tucked the contents of my blog.  I realized that what I wanted was a simple engine that would handle the work of converting Markdown to HTML and stitching the results together with templates, but would otherwise stay out of the way as much as possible, impose little structure and even less of its own code on me, and give me total control over the design without relying on theming.

I was also eager to address a few specific issues:

- It was *only* a blog, lacking even a bio page.
- Responsiveness was questionable.
- Syntax highlighting was not supported.
- I wanted to add a simple display of recent GitHub activity.

Lastly, as Clojure is quickly eclipsing all others as my hacking language of choice, I was heavily biased towards finding a solution that was written in and used it.

### Components

In the end, I selected the following:

- [Bootstrap 3](http://getbootstrap.com) - newly released, rebuilt and responsive-first.
- [Flatly](http://bootswatch.com/flatly/) theme from [Bootswatch](http://bootswatch.com/) - a flat, simple and readable theme for Bootstrap 3.
- [Static](https://github.com/nakkaya/static) - a tiny, embeddable static site generator in Clojure.
- [Font Awesome](http://fortawesome.github.io/Font-Awesome/) - high-quality icons.
- [google-code-prettify](https://code.google.com/p/google-code-prettify/) - code syntax highlighting.

### Static

Static is a very simple static site generator, with full documentation that spans about [two pages](http:/nakkaya.com/static.html).  What's most refreshing about Static (compared to Octropress, at least) is that it's built as a separate project, and then the .jar is copied into the repo for the site that will use it.  This means that the only traces of it that end up in the blog project are the .jar itself, and a few, flexible conventions regarding directory structure.

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

At minimum, this is the default structure of files and directories needed for a site:

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
- `templates` - Full-page Hiccup templates.

All that's needed to build the site is this:

<!--?prettify lang=sh-->

    java -jar static-app.jar --build

The `--watch` option can be used to rebuild automatically when a file changes.  When the site builds, something like the following should result:


    [+] INFO: Using tmp location: /var/folders/r5/30xb2fj573b_s9_2f18y4s_00000gn/T/static/
    [+] INFO: Processing Public  0.011 secs
    [+] INFO: Processing Site  0.213 secs
    [+] INFO: Processing Posts  0.695 secs
    [+] INFO: Creating RSS  0.07 secs
    [+] INFO: Creating Tags  0.03 secs
    [+] INFO: Creating Sitemap  0.0040 secs
    [+] INFO: Creating Aliases  0.01 secs
    [+] INFO: Build took  1.034 secs

An `html` directory will be created in the root of the site, containing all the generated HTML.  I found that pointing my local nginx at this folder was the most straightforward way to serve the site locally while working on it, although Static does offer a `--jetty` option to serve it as well.  The contents of my `config.clj` are as follows:

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

### HTML templating with Hiccup

Static uses [Hiccup](https://github.com/weavejester/hiccup), a great templating library for Clojure, to specify the structure of pages it generates.  Having never used it before, I instantly found it to be very natural and efficient &mdash; the syntax is extremely minimal, vectors and maps are used for elements and their attributes, respectively, and it's possible to embed Clojure code right along with element definitions.

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

Note the access of the `:description` and `:tags` from `metadata`.  Static injects a few values into page rendering, specifically `metadata` and `content`.  `metadata` provides some information about what kind of page is being rendered, as well as the metadata specified in the headers of blog posts, while `content` is the actual Markdown or Hiccup-generated content that the template will include.  Because of this, it's possible to specify different behaviors depending on what's being rendered: 

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

Above, if the page is a post, a simple Bootstrap grid is created, followed by the standard JS to include Disqus comments.  Note the terse syntax for specifying element classes:  this is actually one of two possible syntaxes to define classes and ids.  Below, these two forms are equivalent:

<!--?prettify lang=clj-->

	[:div {:class "col-md-12"} "..."]
	[:div.col-md-12 "..."]

In the absence of a ready way to list blog post titles and dates, I found and adapted some code from the site of Static's [author](http://nakkaya.com/).  A number of functions are made available within templates, although they are largely undocumented:

<!--?prettify lang=clj-->

    [:div.row
     [:div.col-md-6
      [:h4 "Recent Blog Posts"]
      (map #(let [f % url (static.core/post-url f)
                  [metadata _] (static.io/read-doc f)
                  date (static.core/parse-date
                        "yyyy-MM-dd" "dd MMMM yyyy"
                        (re-find #"\d*-\d*-\d*" (str f)))]
         [:div
          [:div [:a {:href url} (:title metadata)]
          [:div date]]])
         (take 5 (reverse (static.io/list-files :posts))))]


### Bootstrap 3, Font Awesome, and theming

Fortunately, Bootstrap 3 was nearing release as I was beginning to work on the site, so I grabbed the RC2 version and went to work.  [Bootswatch](http://bootswatch.com/) provides a nice selection of attractive, free themes for Bootstrap 3, of which I picked [Flatly](http://bootswatch.com/flatly/). [Font Awesome](http://fortawesome.github.io/Font-Awesome/) has high-quality icons for Twitter, GitHub and LinkedIn (amongst many, many others), making it an easy choice here.

There are plenty of great starting points / tutorials already out there for Bootstrap (I'd recommend this [starter template](http://getbootstrap.com/getting-started/#template)).  I did make some adjustments to the Flatly theme, though, with the goal of making the site a bit easier on the reader's eyes and more suitable for text-dense pages:

- Changed the standard font to **Source Sans Pro** (from the default **Lato**)
- Changed the code font to **Source Code Pro** (from the default **Monaco**).
- Increased line-height to 24px.
- Narrowed the container max-width to 840px.

The fonts can be found at [Google Fonts](http://www.google.com/fonts).

### Github Activity

While there are some JS libraries to access the GitHub API, my needs were so simple that I was unwilling to introduce additional dependencies to just to parse a little bit of JSON and generate a few DOM elements.  For the same reason, while I ordinarily would be using libraries like [underscore.js](http://underscorejs.org) and [moment.js](http://momentjs.org) for dates, templating or even iteration, here I opted for vanilla JS.

The full code to retrieve, process and display my GitHub commits can be found [here](https://github.com/crsmithdev/crsmithdev.com/blob/master/resources/public/js/crsmithdev.js).  I needed a function to retrieve some JSON from GitHub, transform some of that data into a list of DOM elements, and then append those elements to any containers matching a certain CSS selector:

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

Parsing the JSON is straightforward, as every event that involves a commit will have a `payload.commit` property containing an array of commits.  Using arrays and a native `.join()` function should be preferred to string concatenation, in the absence of templating:

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

Originally I attempted to use [highlight.js](http://softwaremaniacs.org/soft/highlight/en/), but quickly ran into issues:  nearly all of the guesses it made about what kind of syntax was being presented were wrong, and it was difficult to override its default guessing behavior, especially given that I was writing the posts in Markdown, not raw HTML.  Fortunately, [google-code-prettify](https://code.google.com/p/google-code-prettify/) was a much more usable option, even though it does require an [extension](https://code.google.com/p/google-code-prettify/source/browse/trunk/src/lang-clj.js) to handle Clojure.

If I posts *were* written HTML, using google-code-prettify would look something like this:

<!--?prettify lang=html-->

    <pre class="prettyprint lang-clj"><code>
       [:h3 "Interests & Areas of Expertise"]
        [:ul
         [:li "API design, development and scalability"]
         [:li "Distributed systems and architecture"]
         [:li "Functional programming"]
         ; ...
    </code></pre>

But since posts are written in Markdown, that isn't an option.  There's no way to add a class to the auto-generated `<pre><code>...</code></pre>` blocks, and although I could have used literal HTML instead, that brings with it some other issues (angle brackets in code then have to be manually escaped, for example).  Fortunately, google-code-prettify allows the use of directives in HTML comments preceding code blocks, meaning this works the same as the above:

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

Of course, the last of these is the most difficult for me, which is often a sign that it's the most important.

