<a href="https://travis-ci.org/algofi/polymer-minimizer" title="Latest push build on default branch: " target="_polymer-minimizer-build-ci">
            <img src="https://travis-ci.org/algofi/polymer-minimizer.svg?branch=master" alt="build:">
          </a>

<a href="https://sonarqube.com/api/badges/gate?key=fr.algofi.maven.plugins.polymer-minifier" title="Polymer Miminizer Quality Portal (SonarQube)" target="_polymer-minimizer-sonarqube">
<img src="https://sonarqube.com/api/badges/gate?key=fr.algofi.maven.plugins.polymer-minifier" alt="Quality Gate">
</a>


This maven-plugin build a set of polymer elements into a single file and **minifying** each of them.

### The following rules are applied

* HTML blanks removal
* polymer custom tag and properties minification
* Javascript minification (version 2.x)

### Usage

TODO maven configuration example


### Roadmap

#### 1.x
* HTML minification
* polymer custom tag and properties minification
* single file built

#### 2.x

Javascript minification
* Javascript compile with [Google Closure Compiler](https://github.com/google/closure-compiler) is **not working**
* Use an hand made minifier that remove blanks in Javascript and rename minimized polymer component properties.

* each source file is minified separatly and written down to the disk.
* each minified file is also gzipped

#### 3.x (soon)
* CSS minification
* CSS cleaning : removing custom css properties not used
I plan to use the [YUI Compressor](https://yui.github.io/yuicompressor/)

#### 4.x (soon)
* helper to diagnose your HTML imports tree and size
* ...
