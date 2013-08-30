;;(doctype :xhtml-transitional)
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
  [:link {:rel "stylesheet" :type "text/css" :href "//cdnjs.cloudflare.com/ajax/libs/font-awesome/3.2.1/css/font-awesome.min.css"}]
  [:link {:rel "stylesheet" :type "text/css" :href "/css/bootstrap.min.css"}]
  [:link {:rel "stylesheet" :type "text/css" :href "/css/crsmithdev.css"}]
  [:title (:title metadata)]]
 [:body
  [:div.header
   [:nav.navbar.navbar-fixed-top {:role "navigation"}
    [:div.container
     [:div.navbar-header
      [:button.navbar-toggle {:type "button" :data-toggle "collapse" :data-target ".navbar-ex1-collapse"}
      [:span.sr-only "Toggle navigation"]
      [:span.icon-bar]
      [:span.icon-bar]
      [:span.icon-bar]]
      [:a.navbar-brand {:href "/index.html"} "Chris Smith"]]
     [:div.collapse.navbar-collapse.navbar-ex1-collapse
      [:ul.nav.navbar-nav
       [:li [:a {:href "/blog.html"} "Blog"]]
       [:li [:a {:href "/projects.html"} "Projects"]]
       [:li [:a {:href "/about.html"} "About"]]]
      [:ul.nav.navbar-nav.navbar-right
       [:li [:a.icon-header {:href "http://github.com/crsmithdev"} [:i.icon-github.icon-2x]]
       [:li [:a.icon-header {:href "http://twitter.com/crsmithdev"} [:i.icon-twitter.icon-2x]]
       [:li [:a.icon-header {:href "http://www.linkedin.com/in/crsmithdev"} [:i.icon-linkedin.icon-2x]]]]]]]]]]
  [:div.content
   [:div.container
    (if (= (:type metadata) :post)
      [:div.row
       [:div.col-md-12
        content
        [:div#disqus_thread]
        [:script {:type "text/javascript"}
         "var disqus_shortname = 'crsmithdev';"
         "var disqus_identifier = 'http://crsmithdev.com' + window.location.pathname;"
         "var disqus_url = disqus_identifier;"
         "(function() {"
            "var dsq = document.createElement('script'); dsq.type = 'text/javascript'; dsq.async = true;"
            "dsq.src = '//' + disqus_shortname + '.disqus.com/embed.js';"
            "(document.getElementsByTagName('head')[0] || document.getElementsByTagName('body')[0]).appendChild(dsq);"
         "})();"]]]
      content)
    [:script {:src "//cdnjs.cloudflare.com/ajax/libs/jquery/2.0.3/jquery.min.js"}]
    [:script {:src "//cdnjs.cloudflare.com/ajax/libs/twitter-bootstrap/3.0.0-rc2/js/bootstrap.min.js"}]
    [:script {:src "//cdnjs.cloudflare.com/ajax/libs/highlight.js/7.3/highlight.min.js"}]
    [:script {:src "/js/crsmithdev.js"}]
    [:script {:type "text/javascript"}
     "(function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){"
     "(i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),"
     "m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)"
     "})(window,document,'script','//www.google-analytics.com/analytics.js','ga');"
     "ga('create', 'UA-40826256-2', 'crsmithdev.com');"
     "ga('send', 'pageview');"]]]
  [:div.footer
   [:div.container
    [:div.row
     [:div.col-md-12
      [:p "Built with "
       [:a {:href "http://getbootstrap.com/"} "Bootstrap"] " and "
       [:a {:ref "https://github.com/nakkaya/static"} "Static"]
       [:br]
       "&copy; 2013 " [:a {:href "http://crsmithdev.com"} "Chris Smith"]]]]]]]]
