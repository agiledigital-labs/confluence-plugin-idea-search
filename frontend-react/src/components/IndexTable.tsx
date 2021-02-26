import { DynamicTableStateless as DynamicTable } from "@atlaskit/dynamic-table";
import Textfield from "@atlaskit/textfield";
import axios from "axios";
import queryString from "query-string";
import React, { useEffect, useState } from "react";
import { FormDataType, IndexItem, version } from "./index";
import { flow, get, isNil, lowerCase, omitBy, set, startCase } from "lodash/fp";
import { HeadType } from "@atlaskit/dynamic-table/types";
import { makeStyles } from "@material-ui/core";

type FormData = {
  creator: {
    key: { userkey: string };
    name: string;
    lowerName: string;
  };
  indexData: Array<IndexItem>;
  title: string;
  url: string;
};

const AJS = window.AJS ? window.AJS : undefined;

// the default rendered page for paginated table
const defaultPage: number = 1;

const useStyles = makeStyles(() => ({
  root: {
    margin: "1px!important",
  },
}));

const HeaderElement = ({ header, searchTerm, source, handleChange }: any) => {
  const classes = useStyles();

  return (
    <>
      <div>{startCase(header)}</div>
      <Textfield
        id={header}
        value={get(source, searchTerm)}
        className={`${classes.root} sorter-false parser-false`}
        onChange={(e: React.ChangeEvent<HTMLInputElement>) => {
          console.log(e.target.value);
          handleChange(source, e.target.value);
        }}
      />
    </>
  );
};

const headers = (
  handleChange: (key: string, value: string) => void,
  searchTerm: { [p: string]: string | number } | undefined,
  header?: { key: string; source: string }[]
): HeadType => {
  if (!headers) {
    return { cells: [] };
  }
  const headerCells: HeadType = {
    // @ts-ignore
    cells: header?.map(({ key: header, source }, index) => ({
      key: header,
      content: (
        <HeaderElement {...{ header, searchTerm, source, handleChange }} />
      ),
    })),
  };

  return headerCells;
};

const OuterTable = () => {
  // gets context path from atlassian
  // if not found, set to confluence as default
  const contextPath = AJS?.contextPath() ? AJS.contextPath() : "";

  // rows to be shown on each page of the paginated table
  const rowsPerPage = 10;

  const [formData, setFormData] = useState<FormDataType>();

  const [justPages, setJustPages] = useState<Array<FormData>>();

  const handleChange = (term: string, value: string | number) => {
    console.log(term);
    return flow(
      set(term.toLowerCase(), value),
      omitBy(!isNil),
      setSearchTerm
    )(searchTerm);
  };

  // populate form data with a schema from the database
  useEffect(() => {
    axios
      .get(`${contextPath}/rest/form-data/${version}/schema`)
      .then((response) => {
        setFormData({
          indexSchema: JSON.parse(response.data.indexSchema),
        });
      });
  }, []);

  const headersList = formData?.indexSchema?.index?.map((indexSchemaItem) => ({
    key: indexSchemaItem.key,
    source: `${
      lowerCase(indexSchemaItem.type) !== "static"
        ? indexSchemaItem.type
        : indexSchemaItem.key
    }${
      lowerCase(indexSchemaItem.type) !== "static" ? indexSchemaItem.index : ""
    }`,
  }));

  // search term will be empty fields on initial render
  const [searchTerm, setSearchTerm] = useState<{
    [key: string]: string | number;
  }>({
    string0: "",
    number0: "",
    boolean0: "",
    string1: "",
    number1: "",
    boolean1: "",
    string2: "",
    number2: "",
    boolean2: "",
    string3: "",
    number3: "",
    boolean3: "",
    string4: "",
    number4: "",
    boolean4: "",
    title: "",
  });

  useEffect(() => {
    axios
      .get(
        `${contextPath}/rest/form-data/${version}/form-data?${queryString.stringify(
          searchTerm ? searchTerm : {}
        )}`
      )
      .then((response) => {
        setJustPages(response.data);
      });
  }, [searchTerm, contextPath]);

  const order: string[] | undefined = formData?.indexSchema?.index?.map(
    (item) => item.key
  );

  const content = (page: FormData) =>
    order
      ? order
          .map((itemKey) => page.indexData.find((inx) => inx.key === itemKey))
          .map((row, i) => ({
            key: row?.key,
            content: i === 0 ? <a href={page.url}>{row?.value}</a> : row?.value,
          }))
      : [];

  const rows = justPages?.map((page: FormData) => ({
    key: `row-${page.title}`,
    cells: content(page),
  }));

  if (!headersList || !headersList[0].source) {
    return <div>loading...</div>;
  }

  const head = headers(handleChange, searchTerm, headersList);

  return (
    <div>
      <DynamicTable
        head={head}
        rows={rows}
        rowsPerPage={rowsPerPage}
        defaultPage={defaultPage}
        loadingSpinnerSize="large"
        isFixedSize
        isLoading={false}
      />
    </div>
  );
};

export default OuterTable;
