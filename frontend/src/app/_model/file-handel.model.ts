import { SafeUrl } from "@angular/platform-browser";

export interface FileHandel {
 
    file: File,
    url: SafeUrl,
    type: string; 
    picByte: string;
}