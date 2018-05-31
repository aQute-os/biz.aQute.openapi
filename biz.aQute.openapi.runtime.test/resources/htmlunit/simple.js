var data;

var createCORSRequest = function(method, url) {
  var xhr = new XMLHttpRequest();
  if ("withCredentials" in xhr) {
    // Most browsers.
	  console.log(url)
    xhr.open(method, url, true);
  } else if (typeof XDomainRequest != "undefined") {
    // IE8 & IE9
    xhr = new XDomainRequest();
    xhr.open(method, url);
  } else {
    // CORS not supported.
    xhr = null;
  }
  return xhr;
};

var url = '${url}';
var method = 'GET';
var xhr = createCORSRequest(method, url);

xhr.onload = function(e) {
	data = 1
};

xhr.onerror = function() {
	data = -1
	console.log('"' + xhr.responseText + '"')
};

xhr.send();
