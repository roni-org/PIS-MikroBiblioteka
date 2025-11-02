import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatButtonModule } from '@angular/material/button';

import { App } from './app';

@NgModule({
  imports: [
    BrowserModule,
    BrowserAnimationsModule, // REQUIRED for Angular Material
    MatToolbarModule,
    MatButtonModule
  ],
  bootstrap: [App]
})
export class AppModule { }
