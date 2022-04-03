# IITJ_Auth
Trying to automate the Firewall Authentication at IITJ. The authentication process consists of making 2 requests. First a GET request to gstatic generate_204 which 
redirects us to the Captive portal of the WiFi and gives us the magic key required to make a POST request with the username and password to the redirected
Url. Intially porting this to android by using Foreground service with android's WorkManger API to run in background and automate this auth process.
