import { DynamicTableStateless as DynamicTable } from "@atlaskit/dynamic-table";
import Textfield from "@atlaskit/textfield";
import axios from "axios";
import queryString from "query-string";
import React, { useEffect, useState } from "react";
import { FormSchemaType, IndexItem, version } from "./index";
import {
  contains,
  flow,
  get,
  isNil,
  lowerCase,
  omitBy,
  set,
  startCase,
} from "lodash/fp";
import { HeadCellType, HeadType } from "@atlaskit/dynamic-table/types";
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
          handleChange(source, e.target.value);
        }}
      />
    </>
  );
};

// Construct the header elements for the table
const headers = (
  handleChange: (key: string, value: string) => void,
  searchTerm: { [key: string]: string | number } | undefined,
  header?: { key: string; source: string }[]
): HeadType => {
  if (!header) {
    return { cells: [] };
  }
  return {
    cells: header?.map(
      ({ key: header, source }, index): HeadCellType => ({
        key: header,
        content: (
          <HeaderElement {...{ header, searchTerm, source, handleChange }} />
        ),
      })
    ),
  };
};

const OuterTable = () => {
  // gets context path from atlassian
  // if not found, set to confluence as default
  const contextPath = AJS?.contextPath() ? AJS.contextPath() : "";

  // rows to be shown on each page of the paginated table
  const rowsPerPage = 25;

  const [formSchema, setFormSchema] = useState<FormSchemaType>();

  const [justPages, setJustPages] = useState<Array<FormData>>();

  const handleChange = (term: string, value: string | number) =>
    flow(
      set(term.toLowerCase(), value),
      omitBy(!isNil),
      setSearchTerm
    )(searchTerm);

  // populate form data with a schema from the database
  useEffect(() => {
    axios
      .get(`${contextPath}/rest/form-data/${version}/schema`)
      .then((response) => {
        setFormSchema({
          indexSchema: JSON.parse(response.data.indexSchema),
        });
      });
  }, []);

  const headersList = formSchema?.indexSchema?.index?.map(
    (indexSchemaItem) => ({
      key: indexSchemaItem.key,
      source: `${
        lowerCase(indexSchemaItem.type) !== "static"
          ? indexSchemaItem.type
          : indexSchemaItem.key
      }${
        lowerCase(indexSchemaItem.type) !== "static"
          ? indexSchemaItem.index
          : ""
      }`,
    })
  );

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

  const definedColumnOrder:
    | string[]
    | undefined = formSchema?.indexSchema?.index?.map((item) => item.key);

  const uiWidget = formSchema?.indexSchema?.index
    ?.filter((item) => item["ui:widget"])
    .map((item) => item.key);

  const content = (page: FormData) =>
    definedColumnOrder
      ? definedColumnOrder
          .map((itemKey) => page.indexData.find((inx) => inx.key === itemKey))
          .map((row, i) => {
            if (contains(row?.key)(uiWidget)) {
              const information = row?.value
                ? JSON.parse(row.value as string)
                : {};
              return {
                key: row?.key,
                content: information.url ? (
                  <a href={`${contextPath}${information.url}`}>
                    {" "}
                    {information.username}
                  </a>
                ) : information.username ? (
                  information.username
                ) : (
                  "not found"
                ),
              };
            } else {
              return {
                key: row?.key,
                content:
                  i === 0 ? <a href={page.url}>{row?.value}</a> : row?.value,
              };
            }
          })
      : [];

  const rows = justPages?.map((page: FormData) => ({
    key: `row-${page.title}`,
    cells: content(page),
  }));

  if (!headersList || !headersList[0].source) {
    return (
      <div>
        No index rows found in the configuration. These need to be set in the
        admin section of Confluence
      </div>
    );
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
