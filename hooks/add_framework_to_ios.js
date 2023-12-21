var xcode = require('xcode'),
    fs = require('fs'),
    path = require('path');

function getProjectName() {
    var config = fs.readFileSync('config.xml').toString();
    var parseString = require('xml2js').parseString;
    var name;
    parseString(config, function (err, result) {
        name = result.widget.name.toString();
        const r = /\B\s+|\s+\B/g;  // Removes trailing and leading spaces
        name = name.replace(r, '');
    });
    name = name.replace(/\s/g, '\\ ');  // Escape spaces for file paths
    return name || null;
}

module.exports = function(context) {
    var projectPath = path.join('platforms/ios/' + getProjectName() + '.xcodeproj/project.pbxproj');
    var myProj = xcode.project(projectPath);

    myProj.parseSync();

    // Add TMXProfilingConnections framework
    var frameworkPath = path.join('plugins/TMXProfilingPlugin/src/ios/frameworks/TMXProfilingConnections.xcframework');
    myProj.addFramework(frameworkPath, {customFramework: true, embed: true});

    // Modify build settings to ensure framework is set to "Embed & Sign"
    var configurations = myProj.pbxXCBuildConfigurationSection();
    for (var key in configurations) {
        var config = configurations[key];
        if (typeof config === 'object') {
            var buildSettings = config.buildSettings;
            buildSettings['CODE_SIGN_IDENTITY[sdk=iphoneos*]'] = '"iPhone Developer"';
            buildSettings['LD_RUNPATH_SEARCH_PATHS'] = '"$(inherited) @executable_path/Frameworks"';
        }
    }

    // Write the modified project back to disk
    fs.writeFileSync(projectPath, myProj.writeSync());
};
