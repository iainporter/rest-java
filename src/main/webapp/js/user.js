javaRest.user = {}


/**
 * Get user info
 * @param {function}
 */
javaRest.user.get = function (callback) {


  var userResponse = store.get('userResponse')


  if (userResponse) {
    var response = JSON.parse(userResponse)
    javaRest.user.user = response.user
    // We still download the latest data in the background to make sure
    // cache is current. But we return immediately.
    javaRest.user.download(callback)
    return callback()
  }


  javaRest.user.download(callback)

}

/**
 * @return {bool}
 */
javaRest.user.is_logged_in = function () {
  return !!javaRest.cookie.get('token')
}

/**
 * Log the user in
 * @param {string}
 * @param {string}
 * @param {function} Callback. First parameter is error, if any.
 */
javaRest.user.login = function (email, password, callback) {

  javaRest.post(
    'user/login',
      {
      "username" : email,
      "password" : password
      },
    function (response) {
      javaRest.cookie.set('token', response.token)
      javaRest.cookie.set('userId', response.userId)
      javaRest.cookie.set('email', email)
      callback()

    },
    function(jqXHR, textStatus) {
      callback(jqXHR)
    })

}

/**
 * Log the user in via facebook
 * @param {string}
 * @param {function} Callback. First parameter is error, if any.
 */
javaRest.user.loginSocial = function (accessToken, callback) {

  javaRest.post(
    'user/login/facebook',
      {
      "accessToken" : accessToken
      },
    function (response) {
      javaRest.cookie.set('token', response.token)
      javaRest.cookie.set('userId', response.userId)
      callback()
      
    },
    function(jqXHR, textStatus) {
      callback(jqXHR)
    })
  
}


/**
 * Delete the users cookies.
 */
javaRest.user.logout = function () {
  javaRest.cookie.remove('token')
  javaRest.cookie.remove('userId')
  javaRest.cookie.remove('email')
  store.clear()
  window.location = 'index.html'
}

/**
 * Delete the users cookies.
 */
javaRest.user.reset_password = function (token, password, callback) {
  javaRest.post(
    'password/tokens/' + token,
      {
      "password" : password
      },
    function (response) {
      callback()      
    },
    function(jqXHR, textStatus) {
      callback(jqXHR)
    })
}

/**
 * Delete the users cookies.
 */
javaRest.user.send_reset_email = function (email, callback) {
  javaRest.post(
    'password/tokens',
      {
      "emailAddress" : email
      },
    function (response) {
      callback()
      
    },
    function(jqXHR, textStatus) {
      callback(jqXHR)
    })
}


