/**
 * Singleton used for Namespace
 */
function javaRest() {
  
}

/**
 * Wrap the API so we can proxy calls while testing.
 */
javaRest.get = function (url, data, success, error) {
  
  var time = javaRest.get_iso_date()
  var nonce = makeRandomString()
  var string_to_hash = javaRest.cookie.get('token') + ':' + url + ',GET,' + time + "," + nonce
  var authorization = javaRest.cookie.get('userId') + ':' + javaRest.hash(string_to_hash)
  
  var request = $.ajax({
    url: url,
    type: "GET",
    data: data,
    headers: {
      'Authorization' : authorization,
      'x-java-rest-date' : time,
      'nonce' : nonce
    },
    dataType: "json"
  })

  request.done(success)

  request.fail(error)
  
}

function makeRandomString() {
    return Math.random().toString(36).substring(2, 15) +
        Math.random().toString(36).substring(2, 15);
}

/**
 * Return the current time as an ISO 8061 Date
 * @return {string} 2012-06-30T12:00:00+01:00
 */
javaRest.get_iso_date = function () {
  var d = new Date()
  function pad(n) {return n<10 ? '0'+n : n}
  return d.getUTCFullYear()+'-'
    + pad(d.getUTCMonth()+1)+'-'
    + pad(d.getUTCDate())+'T'
    + pad(d.getUTCHours())+':'
    + pad(d.getUTCMinutes())+':'
    + pad(d.getUTCSeconds())+'Z'
}

/**
 * Get a query string var
 * @param {string}
 * @return {string}
 */
javaRest.get_query = function (name) {
  var query = window.location.search.substring(1)
  var vars = query.split('&')
  for (var i = 0; i < vars.length; i++) {
      var pair = vars[i].split('=')
      if (decodeURIComponent(pair[0]) == name) {
          return decodeURIComponent(pair[1])
      }
  }
}

/**
 * SHA256, then base64 encode a string
 * @param {string}
 * @return {string}
 */
javaRest.hash = function (string) {
  var hash = CryptoJS.SHA256(string)
  return hash.toString(CryptoJS.enc.Base64)
}

/**
 * Is the visitor on iPhone or Ipad?
 * @return {bool}
 */
javaRest.isIos = function () {
  return (navigator.userAgent.match(/iPad|iPhone|iPod/i) != null)
}

/**
 * Wrap the API so we can proxy calls while testing.
 */
javaRest.post = function (url, data, success, error) {
  
  $.ajax({
    url: url,
    type: "POST",
    contentType: "application/json", // send as JSON
    data: JSON.stringify(data),
    dataType: "json",
    success : success,
    error : error
  })

  
}

/**
 * Post with authentication
 */
javaRest.postAuth = function (url, data, success, error) {
  
  var time = javaRest.get_iso_date()
  var nonce = makeRandomString()
  var string_to_hash = javaRest.cookie.get('token') + ':' + url + ',POST,' + time + "," + nonce
  var authorization = javaRest.cookie.get('userId') + ':' + javaRest.hash(string_to_hash)
  
  $.ajax({
    url: url,
    type: "POST",
    contentType: "application/json", // send as JSON
    data: JSON.stringify(data),
    headers: {
      'Authorization' : authorization,
      'x-java-rest-date' : time ,
      'nonce' : nonce
    },
    dataType: "json",
    success : success,
    error : error
  })

  
}

/**
 * Wrap the API so we can proxy calls while testing.
 */
javaRest.put = function (url, data, success, error) {
  
  var time = javaRest.get_iso_date()
  var nonce = makeRandomString()
  var string_to_hash = javaRest.cookie.get('token') + ':' + url + ',PUT,' + time + "," + nonce
  var authorization = javaRest.cookie.get('userId') + ':' + javaRest.hash(string_to_hash)
  
  $.ajax({
    url: url,
    type: "PUT",
    contentType: "application/json", // send as JSON
    data: JSON.stringify(data),
    headers: {
      'Authorization' : authorization,
      'x-java-rest-date' : time,
      'nonce' : nonce
    },
    dataType: "json",
    success : success,
    error : error
  })

  
}


