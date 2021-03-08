import React, { useCallback, useRef, useState } from "react";
import { debounce } from "lodash";
import axios from "axios";
import {
  Avatar,
  FormControl,
  Input,
  InputLabel,
  List,
  ListItem,
  ListItemIcon,
  ListItemText,
  Popover,
  Typography,
} from "@material-ui/core";
import { makeStyles } from "@material-ui/styles";
import { isEmpty } from "lodash/fp";

interface ConfluenceUser {
  user: {
    type: "known" | string;
    username: string;
    userKey: string;
    profilePicture: {
      path: string;
      width: number;
      height: number;
    };
    displayName: string;
  };
  title: string;
  excerpt: string;
  url: string;
  entityType: "user" | string;
  iconCssClass: string;
}

const useStyles = makeStyles(() => ({
  root: {
    width: "100%",
    zIndex: "3005!important" as any,
  },
  popover: {
    width: "100%",
    padding: "1rem",
    margin: "1px",
  },
}));
const AJS = window.AJS ? window.AJS : undefined;

export const UserSelection = (props: any) => {
  const classes = useStyles();
  const [userList, setUserList] = useState<
    Array<{ username: string; userKey: string; href: string; url: string }>
  >([]);
  const contextPath = AJS?.contextPath() ? AJS.contextPath() : "";
  const userSearch = useCallback(
    debounce((userInput: string) => {
      if (userInput) {
        axios
          .get(`${contextPath}/rest/api/search?cql=user~"${userInput}"`)
          .then((res) =>
            setUserList(
              res.data.results.map(
                ({ user: userOption, url }: ConfluenceUser) => ({
                  username: userOption.username,
                  userKey: userOption.userKey,
                  href: userOption.profilePicture.path,
                  url,
                })
              )
            )
          );
      }
    }, 650),
    []
  );

  const updateUser = (event: React.ChangeEvent<HTMLInputElement>) => {
    props.onChange(JSON.stringify({ username: event.target.value }));
    userSearch(event.target.value);
  };

  const inputEl = useRef(null);
  return (
    <>
      <FormControl fullWidth={true} required={props.required} ref={inputEl}>
        <InputLabel>{props.label}</InputLabel>
        <Input
          disabled={props.disabled || props.readonly}
          id={`${props.id}-key`}
          name={`${props.id}-key`}
          onBlur={!props.readonly ? props.handleBlur : undefined}
          type="text"
          onChange={updateUser}
          className="autocomplete-multiuser MuiInputBase-input MuiInput-input"
          value={
            props?.value &&
            !isEmpty(props?.value) &&
            JSON.parse(props?.value).username
              ? JSON.parse(props?.value).username
              : ""
          }
          required={props.required}
        />
      </FormControl>
      <Popover
        id="user-popover"
        open={userList.length > 0}
        anchorEl={inputEl.current}
        className={classes.root}
        anchorOrigin={{
          vertical: "bottom",
          horizontal: "left",
        }}
        transformOrigin={{
          vertical: "top",
          horizontal: "left",
        }}
      >
        <div className={classes.popover}>
          <Typography>Select a user</Typography>
          <List component="nav" aria-labelledby="nested-list-subheader">
            {userList
              ? userList.map(({ username, userKey, href, url }) => (
                  <ListItem
                    button
                    onClick={() => {
                      props.onChange(
                        JSON.stringify({ username, userKey, url })
                      );
                      setUserList([]);
                    }}
                  >
                    <ListItemIcon>
                      <Avatar alt="username" src={href} />
                    </ListItemIcon>
                    <ListItemText primary={username} />
                  </ListItem>
                ))
              : undefined}
          </List>
        </div>
      </Popover>
    </>
  );
};

export default UserSelection;
