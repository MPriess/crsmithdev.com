var ghActivity = (function() {

    var months = ['January', 'Febuary', 'March', 'April', 'May', 'June', 'July', 'August',
	'September', 'October', 'November', 'December'];

    var activity = function(cls, n) {
	var containers = $(cls);

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

    var commits = function(events, n) {
	
	var elements = []

	for (var i = 0; i < events.length && elements.length < n; ++i) {
	    var event = events[i];

	    if (event.hasOwnProperty('payload') && event.payload.hasOwnProperty('commits')) {

		var repo = event.repo.name.split('/')[1];
		var date = toDateString(event.created_at);

		for (var j = 0; j < event.payload.commits.length; ++j) {
		    var commit = event.payload.commits[j];

		    var arr = ['<div><div><a href=https://github.com/"', event.repo.name, '/commit/',
			commit.sha, '">', commit.message, '</a> <span class="text-muted">', repo,
			'</span></div>', '<div>', date,	'</div></div>'];

		    elements.push($(arr.join('')));
		}
	    }
	}

	return elements;
    };

    return {
	activity: activity
    };
})();

$(function() {
    ghActivity.activity('.gh-recent', 5);
});

