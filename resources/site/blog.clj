{:title "crsmithdev.com - blog"}

[:div
 [:div.row
  [:div.col-md-12
   [:h2 "Blog"]
   (map #(let [f % url (static.core/post-url f)
               [metadata _] (static.io/read-doc f)
               date (static.core/parse-date
                     "yyyy-MM-dd" "dd MMM yyyy"
                     (re-find #"\d*-\d*-\d*" (str f)))]
      [:div
       [:h4 [:a {:href url} (:title metadata)]]
       [:h5.blog-date date]])
      (take 8 (reverse (static.io/list-files :posts))))
   ]]]
