jUnit HTML report
=================

# One time setup
- install Node (any reasonably modern version will do as long as it comes with NPM)
- run `npm` or `yarn` (if you use Yarn)

# To build a static HTML/CSS app
- run `npm run build` or `yarn build` - this will transpile/bundle/minify a simple JS app and inline it into an HTML file (`../test_runner/src/main/resources/inline.html`)

# To spin up a dev server
- run `npm run start` or `yarn start` - this should serve the app with test data and reload whenever source files change.
