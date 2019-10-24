# react-native-smtp (Android Only)

## Getting started

`$ npm install react-native-smtp --save`

### Mostly automatic installation

`$ react-native link react-native-smtp`

### Manual installation


#### iOS

1. In XCode, in the project navigator, right click `Libraries` ➜ `Add Files to [your project's name]`
2. Go to `node_modules` ➜ `react-native-smtp` and add `Smtp.xcodeproj`
3. In XCode, in the project navigator, select your project. Add `libSmtp.a` to your project's `Build Phases` ➜ `Link Binary With Libraries`
4. Run your project (`Cmd+R`)<

#### Android

1. Open up `android/app/src/main/java/[...]/MainApplication.java`
  - Add `import com.reactlibrary.SmtpPackage;` to the imports at the top of the file
  - Add `new SmtpPackage()` to the list returned by the `getPackages()` method
2. Append the following lines to `android/settings.gradle`:
  	```
  	include ':react-native-smtp'
  	project(':react-native-smtp').projectDir = new File(rootProject.projectDir, 	'../node_modules/react-native-smtp/android')
  	```
3. Insert the following lines inside the dependencies block in `android/app/build.gradle`:
  	```
      compile project(':react-native-smtp')
  	```


## Usage
```javascript
import EmailSender from 'react-native-smtp';

// Configuration
EmailSender.config({
  host: 'smtp.host.io',
  port: '465', // Optional. Default to 465
  username: 'username',
  password: 'password',
  isAuth: 'true', // Optional. Default to `true`
  tls: 'true' // Optional. Default to `true`
});

/*
 * Used react-native-fs module to get file path.
 * Keep this array empty if there is no attachment.
 */
const attachments = [
  RNFS.ExternalStorageDirectoryPath + '/Tracklist/file.txt',
  RNFS.ExternalStorageDirectoryPath + '/Tracklist/file_2.txt',
];

// Now send the mail
EmailSender.send(
  {
    from: 'from@email.com',
    to: 'to@email.com,another@email.com',
    subject: 'The subject',
    body: '<h3> Cool Body </h3>';
  },
  attachments, // This second parameter is mandatory. You can send an empty array.
);
```
