const WrmPlugin = require("atlassian-webresource-webpack-plugin");
const path = require("path");

module.exports = {
  module: {
    rules: [
      {
        test: /\.tsx?$/,
        exclude: /node_modules/,
        loader: "ts-loader",
      },
    ],
  },
  // mode: "production",
  entry: {
    indexTable: "./src/IndexTable.tsx",
    adminPage: "./src/AdminPage.tsx",
    editModal: "./src/UpdateFormModal.tsx",
    simpleForm: "./src/SimpleForm.tsx",
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
