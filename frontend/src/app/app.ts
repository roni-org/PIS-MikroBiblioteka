import { Component, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClientModule, HttpClient } from '@angular/common/http';
// import { RouterOutlet } from '@angular/router';

import { MatToolbarModule } from '@angular/material/toolbar';
import { MatButtonModule } from '@angular/material/button';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [MatToolbarModule, MatButtonModule, CommonModule, HttpClientModule],
  templateUrl: './app.html',
  styleUrls: ['./app.css']
})
export class App implements  OnInit{
  protected readonly title = signal('frontend');

  selectedFile: File | null = null;
  uploadSuccess = false;
  uploadError = '';
  files: any[] = [];

  private uploadUrl = 'http://localhost:8081/api/files/upload';
  private listUrl = 'http://localhost:8081/api/files';

  constructor(private http: HttpClient) {}

  ngOnInit(): void {
    this.loadFiles();
  }

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      this.selectedFile = input.files[0];
      this.uploadSuccess = false;
      this.uploadError = '';
    }
  }

  onUpload(): void {
    if (!this.selectedFile) return;

    const formData = new FormData();
    formData.append('file', this.selectedFile);

    this.http.post(this.uploadUrl, formData).subscribe({
      next: () => {
        this.uploadSuccess = true;
        this.selectedFile = null;
        this.loadFiles();
        setTimeout(() => this.uploadSuccess = false, 3000);
      },
      error: (err) => {
        console.error(err);
        this.uploadError = 'Upload failed. Please try again.';
        setTimeout(() => this.uploadError = '', 3000);
      }
    });
  }

  loadFiles(): void {
    this.http.get<any[]>(this.listUrl).subscribe({
      next: (data) => {
        this.files = data;
      },
      error: (err) => {
        console.error('Error loading files:', err);
      }
    });
  }
}
