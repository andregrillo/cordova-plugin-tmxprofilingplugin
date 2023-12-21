const fs = require('fs');
const path = require('path');
const util = require('util');
const writeFile = util.promisify(fs.writeFile);

module.exports = function(context) {
    return new Promise((resolve, reject) => {
        const buildJsonContent = {
            ios: {
                debug: {
                    buildFlag: ["OTHER_LDFLAGS=$(inherited) -ObjC"]
                },
                release: {
                    buildFlag: ["OTHER_LDFLAGS=$(inherited) -ObjC"]
                }
            }
        };

        const projectRoot = context.opts.projectRoot;
        const buildJsonPath = path.join(projectRoot, 'build.json');

        writeFile(buildJsonPath, JSON.stringify(buildJsonContent, null, 2))
            .then(() => resolve())
            .catch((error) => reject(error));
    });
};