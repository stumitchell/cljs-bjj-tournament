# cljs-bjj-tournament

Tournament creation software written in clojurescript

[live app](https://s3-ap-southeast-2.amazonaws.com/cljs-bjj-tournament/resources/public/index.html)

## Overview

This tool's aim is to provide database, draws and timekeeping for a Brazilian Jiu-Jitsu Tournament.

At the moment draws are not provided but timekeeping, competitor, club and division information are

## TODO's

 * CRUD operations for Divisions
 * CRUD operations for Clubs
 * firebase integration

## Setup

Subsequent dev builds can use:

    lein cljsbuild auto dev

Clean project specific out:

    lein clean

Optimized builds:

    lein cljsbuild once release

For more info on Cljs compilation, read [Waitin'](http://swannodette.github.io/2014/12/22/waitin/).

## License

Copyright © 2014 Stuart Mitchell and David Scott

Distributed under the Eclipse Public License either version 1.0 or (at your option) any later version.