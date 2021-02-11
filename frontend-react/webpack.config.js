const WrmPlugin = require("atlassian-webresource-webpack-plugin");
const path = require("path");

module.exports = {
  module: {
    rules: [
      {
        test: /\.tsx?$/,
        exclude: /node_modules/,
        use: {
          loader: "ts-loader",
        },
      },
    ],
  },
  // watch : true,
  entry: {
    form: "./src/Form.ts",
    modal: "./src/UpdateFormModal.tsx",
    idea: "./src/Idea.ts",
    admin: "./src/AdminPage.ts",
  },
  resolve: {
    extensions: [".tsx", ".ts", ".js"],
  },
  plugins: [
    new WrmPlugin({
      pluginKey: "au.com.agiledigital",
      locationPrefix: "frontend/",
      // watch : true,
      resourceParamMap: {
        svg: [
          {
            name: "content-type",
            value: "image/svg+xml",
          },
        ],
      },
      xmlDescriptors: path.resolve(
        "../backend/src/main/resources",
        "META-INF",
        "plugin-descriptors",
        "wr-defs.xml"
      ),
    }),
  ],
  output: {
    filename: "bundled.[name].js",
    path: path.resolve("../backend/src/main/resources/frontend"),
  },
};
