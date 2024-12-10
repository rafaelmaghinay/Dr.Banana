
Dr.Banana App version 1

This app is for our CCS 6 project, created after reading a news article in our local area, where banana groves/farms were infested with a panama disease(Fusarium Wilt). After reading that article, Our group has decided to create this app that detects diseases in banana plants by taking a picture through a camera or importing an image from the gallery, and then classifying that image using our image classification model that uses CNN. The model passes the results back to our app and then gives you recommended actions to take.


## API Reference
this app uses SendGrid for emailing user feedback
#### sending emails

```http
  https://api.sendgrid.com/v3/mail/send
```

| Parameter | Type     | 
rating, feedbackText                |
| :-------- | :------- | :------------------------- |
| `api_key` | `string` | **Required**. Your API key |   I will not be sharing our API key


## Authors
Rafael Maghinay
Karl Gaitera
Gregg Emperado
Jadon Sienes

- [@rafaelmaghinay](https://github.com/rafaelmaghinay/dr.banana)

