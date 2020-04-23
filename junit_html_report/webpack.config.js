const path = require('path');
const TerserPlugin = require('terser-webpack-plugin');
const HtmlWebpackPlugin = require('html-webpack-plugin');
const TsconfigPathsPlugin = require('tsconfig-paths-webpack-plugin');

const { NODE_ENV } = process.env;

const terserOptimisation = {
  minimize: true,
  minimizer: [new TerserPlugin()],
};

const isProduction = typeof NODE_ENV !== undefined && NODE_ENV === 'production';

const mode = isProduction ? 'production' : 'development';
const devtool = isProduction ? false : 'inline-source-map';
const optimization = isProduction ? terserOptimisation : undefined;

module.exports = {
  entry: './src/index.ts',
  target: 'web',
  mode,
  devtool,
  module: {
    rules: [
      {
        test: /\.tsx?$/,
        use: 'ts-loader',
        exclude: /node_modules/,
      },
      {
        test: /\.scss$/, use: [
          {loader: "style-loader"},
          {loader: "css-loader", options: { modules: false }},
          {loader: "sass-loader"},
        ],
      }
    ],
  },
  resolve: {
    plugins: [new TsconfigPathsPlugin()],
    extensions: [ '.tsx', '.ts', '.js' ],
  },
  optimization,
  output: {
    filename: 'bundle.js',
    path: path.resolve(__dirname, 'build'),
    // path: isProduction
    //   ? path.resolve(__dirname, 'test_runner', 'src', 'main', 'resources')
    //   : path.resolve(__dirname, 'build'),
  },
  devServer: {
    contentBase: './build',
  },
  plugins: [
    new HtmlWebpackPlugin({
      inject: false,
      cache: false,
      template: 'index.ejs',
      filename: isProduction ? path.resolve(__dirname, '..', 'test_runner', 'src', 'main', 'resources', 'inline.html') : 'index.html',
      title: 'JUnit Report',
      minify: true,
      inlineBundle: isProduction,
    }),
  ]
};
