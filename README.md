# react-native-smtp

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
import Smtp from 'react-native-smtp';

// TODO: What to do with the module?
Smtp;
```
