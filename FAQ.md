# FAQ (Frequently Asked Questions)

## Q: Apakah perlu install database server (MySQL/PostgreSQL)?
Tidak. Program ini menggunakan **SQLite** yang bersifat file-based. Database otomatis dibuat sebagai file `akademik.db` di folder project saat program pertama kali dijalankan.

## Q: Dimana file database disimpan?
File `akademik.db` dibuat di direktori kerja (working directory) saat program dijalankan. Biasanya di root folder project.

## Q: Bagaimana cara menghapus semua data?
Hapus file `akademik.db`, lalu jalankan program lagi. Tabel akan otomatis dibuat ulang (kosong).

## Q: Kenapa compile error "package academic.model does not exist"?
Pastikan compile dari root folder project dan gunakan flag `-cp "lib/*"`. Lihat Makefile untuk contoh perintah yang benar.

## Q: Bagaimana konversi huruf mutu ke bobot?
| Huruf | Bobot |
|-------|-------|
| A     | 4.0   |
| AB    | 3.5   |
| B     | 3.0   |
| BC    | 2.5   |
| C     | 2.0   |
| D     | 1.0   |
| E     | 0.0   |

## Q: Bagaimana rumus IPK?
IPK = Σ(bobot × sks) / Σ(sks)
