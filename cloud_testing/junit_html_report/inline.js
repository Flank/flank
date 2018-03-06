var fs = require('fs');
var inline = require('web-resource-inliner');
var inputFile = './build/index.html';
var inputString = fs.readFileSync(inputFile, "utf8")

inline.html({
  fileContent: inputString,
  relativeTo: "./build/"
},
function(err, result) {
  if (err) { console.log(err); }
  fs.writeFileSync("./bitrise/inline.html", result);
});
