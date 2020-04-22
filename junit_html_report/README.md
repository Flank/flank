jUnit HTML report
=================

# One time setup
- Install Node (any version will do as long as it comes with NPM)
- `npm` or `yarn` (if you have Yarn installed)

# To build a static HTML/CSS app
`npm run build` or `yarn build` - This will transpile/bundle/minify a simple JS app and inline it into an HTML file.

# To inject data into the app
`npm run inject` or `yarn inject` - This will replace `<!-- DATA -->` with POJO containing actual data

# To spin up a dev server
`npm run start` or `yarn start` - This should serve the app and reload when source files change.
