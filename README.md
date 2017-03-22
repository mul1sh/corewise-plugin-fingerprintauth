# tambaza-plugin-fingerprintauth

This plugin enables fingerprint authentication for the android A370 tablets.      

[More info about the tablet](http://corewise.en.made-in-china.com/product/xeqnEcJUsCrw/China-IP65-Rugged-RFID-Smart-Card-Reader-Tablet-PC.html)


Usage
-----
The `TambazaFingerprintAuth` object provides functions to make interacting with the fingerprint scanner available in the A370 tablets easier, and has two functions to validate or register a big fingerprint in the tablet via the fingerprint scanner.

To add it to your project, run the command

    cordova plugin add tambaza-plugin-fingerprintauth

Methods
-------

- TambazaFingerprintAuth.registerFingerprint
- TambazaFingerprintAuth.validateFingerprint

# API reference

TambazaFingerprintAuth.registerFingerprint
===========================================

This function registers the fingerprint via the scanner and returns the id of the fingerprint stored in flash memory after registration, 
    
```javascript
TambazaFingerprintAuth
.registerFingerprint(function(successID){
	//on fingerprint registration success it returns the id of the fingerprint that has been stored in the flash memory
	//do something with the id i.e. store it in a db or something

},function(error){
    //do some error handling

});
```

TambazaFingerprintAuth.validateFingerprint
===========================================

This function validates the fingerprint via the scanner and returns the id of the fingerprint stored in flash memory after validation, 

```javascript
TambazaFingerprintAuth
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




