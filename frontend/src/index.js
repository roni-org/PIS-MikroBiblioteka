import React, { useState, useEffect } from "react";
import ReactDOM from "react-dom/client";
import logo from "./images/mikroBibliotekaLogo.png";

import AppBar from "@mui/material/AppBar";
import Toolbar from "@mui/material/Toolbar";
import Button from "@mui/material/Button";
import Box from "@mui/material/Box";

import "./styles.css";

const App = () => {
  useEffect(() => {
    return () => {
      clearTimeout(moveTimer);
      clearTimeout(hideTimer);
    };
  }, []);

  return (
    <div>
        <AppBar position="static" color="inherit" className="top-bar">
        <Toolbar>
          <Button className="image-button">
            <img src={logo} alt="Logo" className="logo" />
            mikroBiblioteka
          </Button>
          <Box className="separator" />
          <Button
            className="top-bar-button"
            variant="contained"
            color="primary"
          >
            Upload File
          </Button>
        </Toolbar>
        </AppBar>
      <div className="content">
        <h1>Welcome to mikroBiblioteka</h1>
      </div>
    </div>
  );
};

const root = ReactDOM.createRoot(document.getElementById("root"));
root.render(<App />);
