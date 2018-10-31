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
  var kotlinResource = '../test_runner/src/main/resources/inline.html';
  fs.writeFileSync(kotlinResource, result);
});
