# Barcode Scanning Inventory Management API and Mobile App

- **Description:** A cloud-based API and barcode-scanning mobile inventory management app
- **Technologies:** Google Cloud Datastore (NoSQL), Google App Engine, Python, Android, Java
- **Team Project?:** No
- **My Lead Contributions:** Everything

## How to Execute:
- Web Service: The API is live on Google's Cloud Platform.
    - URL Structure:
        - Host: my-project-1509951488279.appspot.com
        - Resources:
            - **/warehouses**
                - GET: retrieves all warehouses
                - POST: creates new warehouse
            - **/warehouses/[warehouse id]**
                - GET: retrieves warehouse with [warehouse id]
                - PUT: updates warehouse with [warehouse id]
                - DELETE: deletes warehouse with [warehouse id] and sets any pieces of merch currently referencing [warehouse id] to a generic warehouse id of 1, which indicates the merch still exists but is currently not being managed by a specific warehouse
            - **/merch**
                - GET: retrieves all merch
                - POST: creates new merch
            - **/merch/[merch id]**
                - GET: retrieves merch with [merch id]
                - PUT: updates merch with [merch id]
                - DELETE: deletes merch with [merch id]
            - **/merch/[merch id]/warehouses/[warehouse id]**
                - PUT: associates warehouse with [warehouse id] with merch with [merch id]
            - **/merch/search**
                - POST: retrieves all merch but is filtered by searching for merch properties
            - **/merch/sum/[merch name]**
                - GET: computes and outputs sum of all quantities of merch with [merch name]; [merch name] may contain spaces, but spaces must be input as underscores

- Build the Android app in Android Studio and install the resulting APK on an Android device.
    - Latest tested device was a Galaxy Note 2 running Android 4.4.2. Newer OS versions and/or devices may not render the app properly.

## Source Code Locations:
The somewhat convoluted directory structure of the Android app can make finding the core source code a challenge. Here are the paths to the main code.

- **Android Java:**
    - nativeapp/CS496FinalProj/app/src/main/java/cj/cs496finalproj/
- **Android XML Layouts:**
    - nativeapp/CS496FinalProj/app/src/main/res/layout/
- **Web Service:**
    - cloudservice/