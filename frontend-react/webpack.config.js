const path = require("path");

module.exports = {
  devtool: "source-map",
  module: {
    rules: [
      {
        test: /\.tsx?$/,
        exclude: /node_modules/,
        loader: "ts-loader",
      },
    ],
  },
  entry: {
    indexTable: "./src/IndexTable.tsx",
    adminPage: "./src/AdminPage.tsx",
    editModal: "./src/UpdateFormModal.tsx",
    simpleForm: "./src/SimpleForm.tsx",
    dataRenderer: "./src/dataRenderer.ts",
  },
  resolve: {
    extensions: [".tsx", ".ts", ".js"],
  },
  plugins: [],
  output: {
    filename: "bundled.[name].js",
    path: path.resolve("../backend/src/main/resources/frontend"),
  },
};
