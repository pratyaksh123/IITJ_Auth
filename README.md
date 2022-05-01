# IITJ Auth

> Never spend 10 seconds doing something by hand when you can spend 1 month ~~failing~~ **Succeeding** to automate it.
> 
> -Someone

![WhatsApp Image 2022-04-26 at 5 57 36 PM](https://user-images.githubusercontent.com/55044774/165881242-fb3790dd-7e21-4d33-9147-be7ab23fd9f2.jpeg)

<a href='https://play.google.com/store/apps/details?id=com.blockgeeks.iitj_auth'><img alt='Get it on Google Play' src='https://play.google.com/intl/en_us/badges/images/generic/en_badge_web_generic.png' height=90px/></a>

Automates the Firewall Authentication at IITJ. No more sign into network prompts while using IITJ Wifi! This app does everything automatically once started.

### Screenshots
<img src="/Graphics/dashboard_light.png" width="180" height="370" /> <img src="/Graphics/QuickSettigns.png" width="180" height="370" /> <img src="/Graphics/settings_light.png" width="180" height="370" /> <img src="/Graphics/dashboard_dark.png" width="180" height="370" />

### Features
- One-time setup hustle.
- Quick Tile to access the app.
- Battery friendly.
- Implemented according to Material Design guidelines.

#### OEM not supported?:
-  Login Service might fail to work on OEMs like Vivo, which have their own captive portal implementations or aggressive service-killing policies.

#### How it works?
The authentication process is fairly easy, consists of making 2 requests. First a GET request to gstatic generate_204 which 
redirects us to the Captive portal of the WiFi and gives us the magic key required to make a POST request with the username and password to the redirected
Url. Trick is to run this script whenever android see's a wifi has a captive portal, and after that persist the auth session by pinging the keepalive url using the `WorkManger` API.

#### Reporting bugs and feature requests?
Create an issue with the given templates.
