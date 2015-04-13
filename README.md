# sass-maven-plugin

[![Build Status](https://travis-ci.org/learningobjectsinc/sass-maven-plugin.svg?branch=master)](https://travis-ci.org/learningobjectsinc/sass-maven-plugin)

Maven plugin to compile SASS into CSS

This plugin scans for subdirectories of `sassSourceDirectory` called `sass` containing `.scss`
files. For each SASS file, an associated CSS file is created in a relative `css` directory under the
`cssTargetDirectory`.

For example, using the default parameters, if there is a SASS input file at `/src/main/resources/com/mycompany/sass/styles.scss`,
then an associated CSS output file will be created at `/target/classes/com/mycompany/css/styles.css`.

# License
* [MIT License](http://www.opensource.org/licenses/mit-license.php)
