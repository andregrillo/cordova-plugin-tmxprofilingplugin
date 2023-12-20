var xcode = require('xcode'),
    fs = require('fs'),
    path = require('path');

function getProjectName() {
    var config = fs.readFileSync('config.xml').toString();
    var parseString = require('xml2js').parseString;
    var name;
    parseString(config, function (err, result) {
        name = result.widget.name.toString();
        const r = /\B\s+|\s+\B/g;  //Removes trailing and leading spaces
        name = name.replace(r, '');
    });
    // Escape spaces for file paths
    name = name.replace(/\s/g, '\\ ');
    return name || null;
}

module.exports = function(context) {
    var projectPath = path.join(context.opts.projectRoot, 'platforms/ios/' + getProjectName() + '.xcodeproj/project.pbxproj');
    var myProj = xcode.project(projectPath);

    myProj.parseSync();

    // Framework to add
    var framework = path.join(context.opts.projectRoot, 'plugins/TMXProfilingPlugin/src/ios/frameworks/TMXProfilingConnections.xcframework');

    // Add framework to project
    myProj.addFramework(framework, {customFramework: true, embed: true});

    // Write the modified project back to disk
    fs.writeFileSync(projectPath, myProj.writeSync());
};
