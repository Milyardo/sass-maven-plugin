# sass-maven-plugin

[![Build Status](https://travis-ci.org/learningobjectsinc/sass-maven-plugin.svg?branch=master)](https://travis-ci.org/learningobjectsinc/sass-maven-plugin)

Maven plugin to compile SASS into CSS

This plugin scans for subdirectories of `sassSourceDirectory` called `sass` containing `.scss`
files. For each SASS file, an associated CSS file is created in a relative `css` directory under the
`cssTargetDirectory`.

For example, using the default parameters, if there is a SASS input file at `/src/main/resources/com/mycompany/sass/styles.scss`,
then an associated CSS output file will be created at `/target/classes/com/mycompany/css/styles.css`.

To use this plugin, include the following in your `pom.xml`:

    <project>
      ...
      <build>
        <!-- To use the plugin goals in your POM or parent POM -->
        <plugins>
          <plugin>
            <groupId>com.learningobjects</groupId>
            <artifactId>sass-maven-plugin</artifactId>
            <version>1.0</version>
            <executions>
              <execution>
                <goals>
                  <goal>compile</goal>
                </goals>
              </execution>
            </executions>
          </plugin>
          ...
        </plugins>
      </build>
      ...
    </project>

For more, see [Plugin Documentation](http://learningobjectsinc.github.io/sass-maven-plugin/plugin-info.html).

# License
[MIT License](http://www.opensource.org/licenses/mit-license.php)
