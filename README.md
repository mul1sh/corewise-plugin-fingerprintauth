# Corewise-plugin-fingerprintauth

This plugin enables fingerprint authentication for the android A370 tablets.      

[More info about the tablet](https://www.corewise.cn/english/products/802.html)


# Usage

ionic 1 / ES5 javascript
------------------------

The `CorewiseFingerprintAuth` object provides functions to make interacting with the fingerprint scanner available in the A370 tablets easier, and has two functions to validate or register a big fingerprint in the tablet via the fingerprint scanner.

To add it to your project, run the command

    cordova plugin add corewise-plugin-fingerprintauth --save

ionic 2 / Typescript
--------------------

[see demo](https://github.com/mul1sh/corewise-fingerprintauth-demo)

For ionic 2/ typescript users, add the plugin with the same command above, then define an interface Window with a property corewise of data type any. Then also declare a global window variable out side of your class and then use it as follows, preferrably when the ionic platform is ready/ all plugins has been loaded to the DOM.

	interface Window {
        CorewiseFingerprintAuth: any;
    }
    declare var window : Window;
    ...
    export class MyClass{

        this.platform.ready().then(() => {
            
			// to register a fingerprint
			window
			.CorewiseFingerprintAuth
			.registerFingerprint(function(successID){
				//on fingerprint registration success it returns the id of the fingerprint that has been stored in the flash memory
				//do something with the id i.e. store it in a db or something

			},function(error){
				//do some error handling

			});

			// to validate a fingerprint
			window
			.CorewiseFingerprintAuth
			.validateFingerprint(function(successID){
				//on fingerprint validation success it returns the id of the fingerprint matched in the flash memory
				//do something with the id i.e. store it in a db or something

			},function(error){
				//do some error handling

			});
        });
    }


# API reference

Methods
-------

- CorewiseFingerprintAuth.registerFingerprint
- CorewiseFingerprintAuth.validateFingerprint


CorewiseFingerprintAuth.registerFingerprint
===========================================

This function registers the fingerprint via the scanner and returns the id of the fingerprint stored in flash memory after registration, 
    
```javascript
CorewiseFingerprintAuth
.registerFingerprint(function(successID){
	//on fingerprint registration success it returns the id of the fingerprint that has been stored in the flash memory
	//do something with the id i.e. store it in a db or something

},function(error){
    //do some error handling

});
```

CorewiseFingerprintAuth.validateFingerprint
===========================================

This function validates the fingerprint via the scanner and returns the id of the fingerprint stored in flash memory after validation, 

```javascript
CorewiseFingerprintAuth
.validateFingerprint(function(successID){
	//on fingerprint validation success it returns the id of the fingerprint matched in the flash memory
	//do something with the id i.e. store it in a db or something

},function(error){
    //do some error handling

});
```


Supported Platforms
-------------------

- Android


# To Do

For now the plugin supports the only registering and validation of big fingerprints but in the future once the vendor releases better documentation for the API, i'll also support the small fingerprints as well.

# Bugs

- For now there are no known bugs but if you do find one, please create an issue as always and I'll look into it during my spare time :)

# Donate

To support further development and maintenance of this plugin you can donate below

[![](https://www.paypalobjects.com/en_US/i/btn/btn_donateCC_LG.gif)](https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=FU2TZH26C3HQQ)




