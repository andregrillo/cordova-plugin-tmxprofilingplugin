#!/usr/bin/env node

const fs = require('fs');
const path = require('path');
const xml2js = require('xml2js');

module.exports = function(context) {
    const manifestPath = path.join(context.opts.projectRoot, 'platforms/android/app/src/main/AndroidManifest.xml');
    const manifestExists = fs.existsSync(manifestPath);

    if (manifestExists) {
        fs.readFile(manifestPath, 'utf8', function(err, data) {
            if (err) {
                throw new Error('🚨 Unable to find AndroidManifest.xml: ' + err);
            }

            xml2js.parseString(data, function(err, result) {
                if (err) {
                    throw new Error('🚨 Unable to parse AndroidManifest.xml: ' + err);
                }

                const manifest = result.manifest;
                const prefix = 'xmlns:tools';
                const toolsUri = 'http://schemas.android.com/tools';
                const permissionName = 'android.permission.QUERY_ALL_PACKAGES';

                // Check if the tools namespace is already present
                if (!manifest.$[prefix]) {
                    manifest.$[prefix] = toolsUri;
                }

                // Check if the permission is already present
                let permissionExists = false;
                if (!manifest['uses-permission']) {
                    manifest['uses-permission'] = [];
                } else {
                    permissionExists = manifest['uses-permission'].some(function(permission) {
                        return permission.$['android:name'] === permissionName;
                    });
                }

                // Add permission with tools:ignore attribute if it's not already there
                if (!permissionExists) {
                    manifest['uses-permission'].push({
                        $: {
                            'android:name': permissionName,
                            'tools:ignore': 'QueryAllPackagesPermission'
                        }
                    });
                }

                // Build XML from JS object
                const builder = new xml2js.Builder();
                const xml = builder.buildObject(result);

                // Write manifest back to file
                fs.writeFile(manifestPath, xml, 'utf8', function(err) {
                    if (err) throw new Error('🚨 Unable to write into AndroidManifest.xml: ' + err);
                });
            });
        });
    } else {
        throw new Error('🚨 AndroidManifest.xml not found.');
    }
};
