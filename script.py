import requests
from bs4 import BeautifulSoup
import tempfile
import webbrowser

url = "http://www.gstatic.com/generate_204"

payload={}
headers = {
  'Host': 'www.gstatic.com',
  'Connection': 'keep-alive',
  'Cache-Control': 'max-age=0',
  'Upgrade-Insecure-Requests': '1',
  'User-Agent': 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/99.0.4844.84 Safari/537.36',
  'Accept': 'text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9',
  'Accept-Encoding': 'gzip, deflate',
  'Accept-Language': 'en-US,en;q=0.9'
}
# Request to get the magic key , returns HTML
response = requests.request("GET", url, headers=headers, data=payload)

# Use beautiful soup to parse the HTML and get the magic key from the response
soup = BeautifulSoup(response.text, 'html.parser')
# print(soup.prettify())
magic_key = soup.find('input', {'name': 'magic'})['value']
print(magic_key)


url_1 = "https://gateway.iitj.ac.in:1003/"

payload=f'4Tredir=http%253A%252F%252Fwww.gstatic.com%252Fgenerate_204&magic={magic_key}&username=tyagi.6&password=<enterPassword>'
headers = {
  'Host': 'gateway.iitj.ac.in:1003',
  'Connection': 'keep-alive',
  'Content-Length': '112',
  'Cache-Control': 'max-age=0',
  'sec-ch-ua': '" Not A;Brand";v="99", "Chromium";v="99", "Google Chrome";v="99"',
  'sec-ch-ua-mobile': '?0',
  'sec-ch-ua-platform': '"macOS"',
  'Upgrade-Insecure-Requests': '1',
  'Origin': 'https://gateway.iitj.ac.in:1003',
  'Content-Type': 'application/x-www-form-urlencoded',
  'User-Agent': 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/99.0.4844.84 Safari/537.36',
  'Accept': 'text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9',
  'Sec-Fetch-Site': 'same-origin',
  'Sec-Fetch-Mode': 'navigate',
  'Sec-Fetch-User': '?1',
  'Sec-Fetch-Dest': 'document',
  'Referer': f'https://gateway.iitj.ac.in:1003/fgtauth?{magic_key}',
  'Accept-Encoding': 'gzip, deflate, br',
  'Accept-Language': 'en-US,en;q=0.9'
}
# POST request to authenicate with fortinet firewall
response = requests.request("POST", url_1, headers=headers, data=payload)

print(response.text)

# Display HTML in a web browser
html = response.text
with tempfile.NamedTemporaryFile('w', delete=False, suffix='.html') as f:
    url = 'file://' + f.name
    f.write(html)
webbrowser.open(url)
