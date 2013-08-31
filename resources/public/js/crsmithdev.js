function ghActivity(toShow) {
    var ghRecents = $('.gh-recent');

    if (ghRecents.length > 0) {
        $.ajax({
            url: 'https://api.github.com/users/crsmithdev/events',
            dataType: 'jsonp',
            success: function (data) {
                var shown = 0;

                for (var i = 0; i < data.data.length && shown < toShow; ++i) {
                    var r = data.data[i];

		    if (r.hasOwnProperty('payload') && r.payload.hasOwnProperty('commits')) {

			for (var j = 0; j < r.payload.commits.length; ++j) {

			    var commit_message = r.payload.commits[0].message;
			    var commit_url = 'https://github.com/' + r.repo.name + '/commit/' + r.payload.commits[0].sha;
			    var repo_name = r.repo.name.split('/')[1];
			    var parts = r.created_at.split('T')[0].split('-');
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
    hljs.initHighlightingOnLoad();
    ghActivity(5);
});

