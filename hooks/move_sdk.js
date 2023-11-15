var fs = require('fs'), path = require('path');

module.exports = function(ctx) {
    var rootdir = ctx.opts.projectRoot,
    android_dir = path.join(ctx.opts.projectRoot, 'platforms/android');
    
    // Define paths for the new .aar files
    var sdk_file1 = path.join(ctx.opts.projectRoot, 'platforms/android/app/libs/TMXProfiling-7.1-45.aar');
    var sdk_file2 = path.join(ctx.opts.projectRoot, 'platforms/android/app/libs/TMXProfilingConnections-7.1-45.aar');
    
    var dest_sdk_folder = path.join(ctx.opts.projectRoot, 'platforms/android/libs');
    
    // Define destination paths for the new .aar files
    var dest_sdk_file1 = path.join(ctx.opts.projectRoot, 'platforms/android/libs/TMXProfiling-7.1-45.aar');
    var dest_sdk_file2 = path.join(ctx.opts.projectRoot, 'platforms/android/libs/TMXProfilingConnections-7.1-45.aar');

    console.log("Before-Build Hook - rootdir", rootdir);
    console.log("Before-Build Hook - android_dir", android_dir);
    // Log the new file paths
    console.log("Before-Build Hook - sdk_file1", sdk_file1);
    console.log("Before-Build Hook - sdk_file2", sdk_file2);
    console.log("Before-Build Hook - dest_sdk_file1", dest_sdk_file1);
    console.log("Before-Build Hook - dest_sdk_file2", dest_sdk_file2);

    // Check for the existence of both .aar files and the android directory
    if (!fs.existsSync(sdk_file1) || !fs.existsSync(sdk_file2)){
        console.log('One or both of the .aar files not found. Skipping');
        return;
    } else if (!fs.existsSync(android_dir)){
        console.log(android_dir + ' not found. Skipping');
        return;
    }

    // Create the destination folder if it doesn't exist
    if (!fs.existsSync(dest_sdk_folder)){
        console.log("Creating libs folder...");
        fs.mkdirSync(dest_sdk_folder);
    }

    // Copy each .aar file
    console.log('Copy ' + sdk_file1 + ' to ' + dest_sdk_folder);
    fs.createReadStream(sdk_file1).pipe(fs.createWriteStream(dest_sdk_file1));

    console.log('Copy ' + sdk_file2 + ' to ' + dest_sdk_folder);
    fs.createReadStream(sdk_file2).pipe(fs.createWriteStream(dest_sdk_file2));
}
