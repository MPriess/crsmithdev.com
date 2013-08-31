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

	git clone https://github.com/nakkaya/static.git
	cd static
	lein deps
	lein uberjar

This results with a .jar named `static-app.jar` in the `target` directory, which can then be moved into a fresh site repo:

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

	java -jar static-app.jar --build

Or, to rebuild automatically when something is changed:

	java -jar static-app.jar --watch

This will result in an 'html' directory appearing in the root of the site, containing the rendered HTML pages.  I found pointing my local nginx at this folder to be the best way to serve the site locally, although Static does offer a `--jetty` option to run it as well.

Blog post configuration is extremely simple, and should be familiar to anyone who has used Markdown for blogging.  Each post will simple require a short header with a few configuration values, as shown here:

	---
	title: Building Better Email Habits with Mailbox
	---

Static's [documentation](http://nakkaya.com/static.html) lists a few more options for the post header, allowing the specification of tags, an alias, and use of non-default templates.

### HTML templating with Hiccup

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

	[:div.content
	 [:div.container
	  (if (= (:type metadata) :post)
		[:div.row
		 [:div.col-md-12
		  content
		  [:div#disqus_thread]
		  [:script {:type "text/javascript"}
		   "// ...JS for Disqus"]]]
		content)

Here, some extra structure for Bootstrap, and the embedded code for Disqus, are injected in the page, but only if its `metadata` `:type` indicates that it is a post.  Also, note the terse syntax for Hiccup:  this is actually a 'short' form of ID / class specification it offers, which makes these two forms equivalent:

	[:div {:class "col-md-12"} "..."]
	[:div.col-md-12 "..."]

### Bootstrap 3, Font Awesome, and theming

Fortunately, Bootstrap 3 was nearing release as I was beginning to work on the site, so I grabbed the RC2 version and went to work.  [Bootswatch](http://bootswatch.com/) provides a nice selection of attractive, free themes for Bootstrap 3, of which I picked [Flatly](http://bootswatch.com/flatly/).  As there are plenty of examples / tutorials on how to set up basic layouts with Bootstrap there's little need for one here.

Although Bootstrap does include the free version of [Glyphicons](http://glyphicons.com/), I've used [Font Awesome](http://fortawesome.github.io/Font-Awesome/) extensively both a work and personally, and find the selection both much larger and of higher quality.  It has built-in, high-quality icons for Twitter, GitHub and LinkedIn (amongst many, many others), making it an easy choice here.

### Github Activity

While there are some JS libraries for the GitHub API, my needs were quite simple here and I was unwilling to introduce more dependencies for a simple, static site.  As there's no need to register with GitHub to use the part of the API I was needing, it should be possible to retrieve, parse and display a list of recent activity using a single `$.ajax` request and some vanilla JavaScript.

My activity on GitHub can be publicly accessed via their API [here](https://api.github.com/users/crsmithdev/events). Here's the full JS required to pull, parse, and display my recent commits:

	function ghActivity(toShow) {
		var ghRecents = $('.gh-recent');

		if (ghRecents.length > 0) {
			$.ajax({
				url: 'https://api.github.com/users/crsmithdev/events',
				dataType: 'jsonp',
				success: function (data) {
					var shown = 0;

					for (var i = 0; i < data.data.length && shown < toShow; ++i) {
						var event = data.data[i];

						if (event.hasOwnProperty('payload') && event.payload.hasOwnProperty('commits')) {
							for (var j = 0; j < event.payload.commits.length; ++j) {

								var commit_message = event.payload.commits[0].message;
								var commit_url = 'https://github.com/' + event.repo.name + '/commit/' + event.payload.commits[0].sha;
								var repo_name = event.repo.name.split('/')[1];
								var parts = event.created_at.split('T')[0].split('-');
								var year = parts[0], month = parts[1], day = parts[2];
								var month_name = ['January', 'Febuary', 'March', 'April', 'May', 'June',
								  'July', 'August', 'September', 'October', 'November', 'December'][parseInt(month) - 1];

								html = '<div><div><a href=\"' + commit_url + '\">' + commit_message + '</a>';
								html += ' <span class=\"text-muted\">' + repo_name + '</span></div>';
								html += '<div>' + day + ' ' + month_name + ' ' + year + '</div></div>';

								ghRecents.append($(html));
								++shown;
							}
						}
					}
				}
			});
		}
	}

	$(function() {
		ghActivity(5);
	});

In a larger application, I'd ordinarily have both [underscore.js](http://underscorejs.org/) and [moment.js](http://momentjs.com/), so there be less code involved in iteration and in handling date parsing.  However, as these would only be required for this specific part of the site, it makes more sense to skip them, and handle things manually.  The same applies for constructing the HTML &mdash; in any less-trivial example, I would certainly opt to use some basic templating, as constructing HTML strings manually can be come very ugly, very fast.

Essentially, every item in the list returned by the GitHub API should have a `payload` element, which may or may not contain a `commits` array (in the case of simply creating a repo, for example, before pushing to it, there are no commits in the payload).  The JS here simply extracts the repo name, commit message, commit url, and date, then builds a simple HTML string from it and injects it into any divs with a `.ghrecent` class.  Of important note is that the GitHub public API has a very low rate limit for public access (60 / hour / IP), so working on and refreshing the index page repeatedly could cause the API to stop responding with data for a short while until the limit is released.

### Deployment

Deployment is extremely straightforward.  I cleared out my existing [crsmithdev.github.com](https://github.com/crsmithdev/crsmithdev.github.com), and copied over all the files from the `html` directory, being sure to add a CNAME file referencing my [crsmithdev.com](http://crsmithdev.com) domain so Pages will work correctly under it.  One push later and all was up and running.

### Future work

I'm much happier with the look and feature set of the site now

- I think the load times could definitely be improved, especially considering this is a static site.  I'll likely have a future post on optimization.
- I need to take a look at how highlight.js both identifies various types of languages, as well as adjust the colors it uses.
- Put simply, I just need to write more!

Of course, the last is the most difficult of the three for me...which also means it is the most important.

