import { Component, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClientModule, HttpClient } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
// import { RouterOutlet } from '@angular/router';

import { MatIconModule } from '@angular/material/icon';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [MatToolbarModule, MatButtonModule, MatIconModule, CommonModule, HttpClientModule, MatFormFieldModule, MatInputModule, FormsModule],
  templateUrl: './app.html',
  styleUrls: ['./app.css'],
})
export class App implements OnInit {
  protected readonly title = signal('frontend');

  selectedFile: File | null = null;
  uploadSuccess = false;
  uploadError = '';
  files: any[] = [];
  filterText: string = '';

  private uploadUrl!: string;
  private listUrl!: string;
  private downloadUrl: string;

  constructor(private http: HttpClient) {
    const { protocol, hostname } = window.location;
    const backendPort = '8081';

    const baseUrl = `${protocol}//${hostname}:${backendPort}`;

    this.uploadUrl = `${baseUrl}/api/files/upload`;
    this.listUrl = `${baseUrl}/api/files`;
    this.downloadUrl = `${baseUrl}/api/files/download`;

  }

  ngOnInit(): void {
    this.loadFiles();
  }

  get filteredFiles() {
    if (!this.filterText) {
      return this.files;
    }
    return this.files.filter(file =>
      file.name.toLowerCase().includes(this.filterText.toLowerCase())
    );
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
        setTimeout(() => (this.uploadSuccess = false), 3000);
      },
      error: (err) => {
        console.error(err);
        this.uploadError = 'Upload failed. Please try again.';
        setTimeout(() => (this.uploadError = ''), 3000);
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


  download(file: any): void {
  const url = `${this.downloadUrl}/${file.id}`;

  this.http.get(url, { responseType: 'blob' }).subscribe({
    next: (blob) => {
      const a = document.createElement('a');
      const objectUrl = URL.createObjectURL(blob);

      a.href = objectUrl;
      a.download = file.name;
      a.click();

      this.loadFiles();

      URL.revokeObjectURL(objectUrl);
    },
    error: (err) => console.error('Download error:', err)
  });
}

delete(file: any): void {
  if (!confirm(`Delete file "${file.name}"?`)) {
    return;
  }

  const url = `${this.listUrl}/${file.id}`;

  this.http.delete(url).subscribe({
    next: () => {
      this.loadFiles();
    },
    error: (err) => console.error('Delete error:', err)
  });
}

}
