/**
 * Holds cookie methods
 */
javaRest.cookie = {}

/**
 * Get the value of a cookie.
 * @param {string}
 * @return {string}
 */
javaRest.cookie.get = function (name) {
  var pairs = document.cookie.split(/\; /g)
  var cookie = {}
  for (var i in pairs) {
    var parts = pairs[i].split(/\=/)
    cookie[parts[0]] = unescape(parts[1])
  }
  return cookie[name]
}

/**
 * Delete a cookie
 * @param {string}
 */
javaRest.cookie.remove = function (name) {
  document.cookie = name + '=; expires=Thu, 01 Jan 1970 00:00:01 GMT;'
}

/**
 * Set a cookie
 * @param {string}
 * @param {string}
 */
javaRest.cookie.set = function (name, value) {
  // document.cookie = "name=value[; expires=UTCString][; domain=domainName][; path=pathName][; secure]"; 
  document.cookie = name + '=' + value;
}

