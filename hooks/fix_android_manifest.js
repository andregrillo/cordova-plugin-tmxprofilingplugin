module.exports = function(context) {
    const fs = context.requireCordovaModule('fs');
    const path = context.requireCordovaModule('path');
    const xml2js = context.requireCordovaModule('xml2js');

    const manifestPath = path.join(context.opts.projectRoot, 'platforms/android/app/src/main/AndroidManifest.xml');
    const manifestData = fs.readFileSync(manifestPath).toString();

    xml2js.parseString(manifestData, (err, manifestObj) => {
        if (err) {
            throw new Error(`Error parsing AndroidManifest.xml: ${err}`);
        }

        // Ensure the manifest has the tools namespace attribute
        manifestObj.manifest.$['xmlns:tools'] = 'http://schemas.android.com/tools';

        // Find and update the QUERY_ALL_PACKAGES permission
        const usesPermissions = manifestObj.manifest['uses-permission'] || [];
        const queryAllPackagesPermission = usesPermissions.find(perms => 
            perms['$']['android:name'] === 'android.permission.QUERY_ALL_PACKAGES');

        if (queryAllPackagesPermission) {
            queryAllPackagesPermission['$']['tools:ignore'] = 'QueryAllPackagesPermission';
        } else {
            // Add the permission if not found
            manifestObj.manifest['uses-permission'].push({
                '$': {
                    'android:name': 'android.permission.QUERY_ALL_PACKAGES',
                    'tools:ignore': 'QueryAllPackagesPermission'
                }
            });
        }

        // Convert the object back to XML
        const builder = new xml2js.Builder();
        const updatedManifest = builder.buildObject(manifestObj);

        // Write the updated manifest back to the file
        fs.writeFileSync(manifestPath, updatedManifest);
    });
};
