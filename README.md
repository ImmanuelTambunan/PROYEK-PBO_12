# Sistem Pengelolaan Nilai dan Rekam Jejak Akademik Mahasiswa

## Deskripsi

Program ini adalah aplikasi CLI (Command Line Interface) berbasis Java yang mensimulasikan proses pendataan nilai mahasiswa di jurusan Sistem Informasi. Data diproses terlebih dahulu di memori menggunakan Java Collection Framework, kemudian disimpan secara permanen ke database SQLite melalui JDBC. Program ini mengintegrasikan **4 konsep utama** dalam satu alur eksekusi yang saling terhubung.

---

## Bagaimana 4 Konsep Ini Bekerja Bersama?

Logikanya sebenarnya sederhana. Coba bayangkan alurnya seperti ini:

Pertama, kita butuh **model data** yang terstruktur. Di dunia kampus, mahasiswa dan dosen sama-sama punya nama dan identitas — jadi masuk akal kalau kita bikin satu "cetakan" umum bernama `CivitasAkademika`, lalu mahasiswa dan dosen dibuat sebagai turunannya. Ini yang disebut **Inheritance**. Mahasiswa punya NIM dan Prodi, Dosen punya NIDN dan Fakultas, tapi keduanya berbagi atribut dasar yang sama.

Kedua, waktu user mengetikkan perintah seperti `TAMBAH_MAHASISWA#1142400#Silvia#Sistem Informasi`, data itu belum langsung masuk ke database. Data ditampung dulu di memori — tepatnya di `ArrayList` dan `HashMap`. Kenapa? Supaya kita bisa mengumpulkan beberapa data sekaligus, memvalidasinya, atau mengaksesnya dengan cepat sebelum di-"flush" ke database. Ini peran **JCF (Java Collection Framework)**.

Ketiga, saat user mengetik `SIMPAN_KE_DB`, barulah semua data di buffer memori dikirim ke database. Di sinilah **JDBC** bekerja — program membuka koneksi ke SQLite, lalu mengeksekusi query INSERT menggunakan `PreparedStatement`. SQLite dipilih karena tidak perlu install server apapun — database-nya cukup satu file `.db`.

Keempat, ada pertanyaan: siapa yang "menerjemahkan" objek Java jadi baris di tabel database, dan sebaliknya? Di sinilah **Custom ORM** berperan. Kelas `AkademikORM` bertugas mengubah objek `Mahasiswa` menjadi `INSERT INTO mahasiswa(nim, nama, prodi) VALUES(?, ?, ?)`, dan sebaliknya — merakit ulang `ResultSet` dari database menjadi objek `Mahasiswa` yang bisa kita pakai di program.

Keempat konsep ini bukan berdiri sendiri-sendiri — mereka terintegrasi dalam satu alur:

```
[User Input via CLI]
        |
        v
[Buat Objek Java] ← INHERITANCE (CivitasAkademika → Mahasiswa/Dosen)
        |
        v
[Tampung di Memori] ← JCF (ArrayList, HashMap)
        |
        v  (SIMPAN_KE_DB)
[Objek → SQL] ← ORM (AkademikORM: Objek → PreparedStatement)
        |
        v
[Eksekusi Query] ← JDBC (PreparedStatement → SQLite)
        |
        v
[Simpan Permanen ke Database]

        ===== saat CETAK_TRANSKRIP =====

[Query Database] ← JDBC (SELECT JOIN)
        |
        v
[ResultSet → Objek] ← ORM (AkademikORM: ResultSet → Nilai)
        |
        v
[Kumpulkan ke List] ← JCF (ArrayList<Nilai>)
        |
        v
[Tampilkan ke User] ← Polymorphism (getPeran() dari Inheritance)
```

---

## Tabel Pemetaan Konsep

| Konsep | File | Penjelasan |
|--------|------|------------|
| **Inheritance** | `CivitasAkademika.java`, `Mahasiswa.java`, `Dosen.java` | `CivitasAkademika` adalah abstract parent class. `Mahasiswa` dan `Dosen` mewarisi atribut (id, nama, unitKerja) dan meng-override metode `getPeran()`. |
| **JCF** | `Driver1.java` | `ArrayList` untuk buffer data sebelum ke DB. `HashMap` untuk lookup cepat berdasarkan NIM/kode MK. Juga dipakai untuk menampung hasil query. |
| **JDBC** | `DatabaseManager.java`, `AkademikORM.java` | `DatabaseManager` mengelola koneksi Singleton ke SQLite. `AkademikORM` mengeksekusi query CRUD via `PreparedStatement`. |
| **ORM (Custom)** | `AkademikORM.java` | Setiap method melakukan pemetaan: `insertMahasiswa()` = Objek → SQL, `selectAllMahasiswa()` = ResultSet → List\<Mahasiswa\>. Tanpa framework seperti Hibernate. |

---

## Struktur Folder

```
2324-ge-t08-transcript/
├── .github/
│   └── workflows/
│       └── classroom.yml           ← CI/CD untuk GitHub Classroom
├── bin/
│   └── academic/
│       ├── driver/                 ← hasil compile (.class)
│       └── model/                  ← hasil compile (.class)
├── lib/
│   └── sqlite-jdbc-3.49.1.0.jar   ← JDBC driver untuk SQLite
├── src/
│   └── academic/
│       ├── driver/
│       │   └── Driver1.java        ← program utama (CLI)
│       └── model/
│           ├── CivitasAkademika.java ← abstract parent class
│           ├── Mahasiswa.java        ← extends CivitasAkademika
│           ├── Dosen.java            ← extends CivitasAkademika
│           ├── MataKuliah.java       ← model mata kuliah
│           ├── Nilai.java            ← model nilai + konversi bobot
│           ├── DatabaseManager.java  ← Singleton JDBC connection
│           └── AkademikORM.java      ← Custom ORM (Objek ↔ SQL)
├── .classpath
├── .gitignore
├── .project
├── changelog.txt
├── FAQ.md
├── Makefile
└── README.md
```

---

## Desain Class (Inheritance)

```
        ┌─────────────────────────┐
        │   CivitasAkademika      │  ← Abstract Parent Class
        │   (abstract)            │
        ├─────────────────────────┤
        │ - id: String            │
        │ - nama: String          │
        │ - unitKerja: String     │
        ├─────────────────────────┤
        │ + getPeran(): String    │  ← abstract (polymorphism)
        │ + getId(), getNama()... │
        └────────┬────────┬───────┘
                 │        │
       ┌─────────┘        └─────────┐
       │                            │
┌──────┴──────────┐   ┌─────────────┴──┐
│   Mahasiswa     │   │     Dosen      │
├─────────────────┤   ├────────────────┤
│ nim = getId()   │   │ nidn = getId() │
│ prodi = unit    │   │ fak = unit     │
│ Kerja           │   │ Kerja          │
├─────────────────┤   ├────────────────┤
│ getPeran()      │   │ getPeran()     │
│ → "Mahasiswa"   │   │ → "Dosen"      │
└─────────────────┘   └────────────────┘
```

---

## Skema SQL (DDL)

Tabel-tabel ini otomatis dibuat oleh `DatabaseManager.initializeDatabase()` saat program pertama kali dijalankan:

```sql
-- Tabel Mahasiswa
CREATE TABLE IF NOT EXISTS mahasiswa (
    nim TEXT PRIMARY KEY,
    nama TEXT NOT NULL,
    prodi TEXT NOT NULL
);

-- Tabel Dosen
CREATE TABLE IF NOT EXISTS dosen (
    nidn TEXT PRIMARY KEY,
    nama TEXT NOT NULL,
    fakultas TEXT NOT NULL
);

-- Tabel Mata Kuliah
CREATE TABLE IF NOT EXISTS mata_kuliah (
    kode TEXT PRIMARY KEY,
    nama TEXT NOT NULL,
    sks INTEGER NOT NULL
);

-- Tabel Nilai (relasi mahasiswa ↔ mata kuliah)
CREATE TABLE IF NOT EXISTS nilai (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    nim TEXT NOT NULL,
    kode_mk TEXT NOT NULL,
    huruf_mutu TEXT NOT NULL,
    FOREIGN KEY (nim) REFERENCES mahasiswa(nim),
    FOREIGN KEY (kode_mk) REFERENCES mata_kuliah(kode),
    UNIQUE(nim, kode_mk)
);
```

---

## Daftar Perintah CLI

| Perintah | Format | Keterangan |
|----------|--------|------------|
| Tambah Mahasiswa | `TAMBAH_MAHASISWA#NIM#Nama#Prodi` | Simpan ke buffer memori |
| Tambah Dosen | `TAMBAH_DOSEN#NIDN#Nama#Fakultas` | Simpan ke buffer memori |
| Tambah Mata Kuliah | `TAMBAH_MATAKULIAH#Kode#Nama#SKS` | Simpan ke buffer memori |
| Input Nilai | `INPUT_NILAI#NIM#KodeMK#HurufMutu` | Simpan ke buffer memori |
| Simpan ke DB | `SIMPAN_KE_DB` | Flush buffer → database (JDBC + ORM) |
| Cetak Transkrip | `CETAK_TRANSKRIP#NIM` | Query DB → tampilkan transkrip + IPK |
| Lihat Semua | `LIHAT_SEMUA` | Tampilkan seluruh data dari database |
| Akhiri Program | `---` | Tutup koneksi dan keluar |

---

## Konversi Huruf Mutu → Bobot

| Huruf Mutu | Bobot |
|------------|-------|
| A | 4.0 |
| AB | 3.5 |
| B | 3.0 |
| BC | 2.5 |
| C | 2.0 |
| D | 1.0 |
| E | 0.0 |

**Rumus IPK:** IPK = Σ(bobot × SKS) / Σ(SKS)

---

## Contoh Simulasi Input dan Output

### Input (via Terminal)

```
TAMBAH_MAHASISWA#1142400#Silvia#Sistem Informasi
TAMBAH_DOSEN#D001#Chandro Pardede, S.Kom., M.Sc.#Fakultas Informatika
TAMBAH_MATAKULIAH#IS301#Sistem Operasi#3
TAMBAH_MATAKULIAH#IS302#Basis Data#3
TAMBAH_MATAKULIAH#IS303#Pemrograman Berorientasi Objek#3
INPUT_NILAI#1142400#IS301#A
INPUT_NILAI#1142400#IS302#AB
INPUT_NILAI#1142400#IS303#B
SIMPAN_KE_DB
CETAK_TRANSKRIP#1142400
LIHAT_SEMUA
---
```

### Output (Ekspektasi)

```
========================================================
  SISTEM PENGELOLAAN NILAI & REKAM JEJAK AKADEMIK
  Inheritance | JCF | JDBC | Custom ORM
========================================================
Masukkan perintah (ketik '---' untuk mengakhiri):

[OK] Mahasiswa ditambahkan ke buffer: Silvia (NIM: 1142400)
[OK] Dosen ditambahkan ke buffer: Chandro Pardede, S.Kom., M.Sc. (NIDN: D001)
[OK] Mata kuliah ditambahkan ke buffer: Sistem Operasi (IS301, 3 SKS)
[OK] Mata kuliah ditambahkan ke buffer: Basis Data (IS302, 3 SKS)
[OK] Mata kuliah ditambahkan ke buffer: Pemrograman Berorientasi Objek (IS303, 3 SKS)
[OK] Nilai ditambahkan ke buffer: NIM 1142400 | MK IS301 | Nilai: A (bobot: 4.0)
[OK] Nilai ditambahkan ke buffer: NIM 1142400 | MK IS302 | Nilai: AB (bobot: 3.5)
[OK] Nilai ditambahkan ke buffer: NIM 1142400 | MK IS303 | Nilai: B (bobot: 3.0)

--- Menyimpan data ke database ---
[OK] 8 record berhasil disimpan ke database.
[OK] Buffer memori dibersihkan.

====================================================
           TRANSKRIP NILAI AKADEMIK
====================================================
NIM    : 1142400
Nama   : Silvia
Prodi  : Sistem Informasi
Status : Mahasiswa
====================================================
Kode       Mata Kuliah                      SKS  Nilai  Bobot
----------------------------------------------------
IS301      Sistem Operasi                   3   A      4.0
IS302      Basis Data                       3   AB     3.5
IS303      Pemrograman Berorientasi Objek   3   B      3.0
----------------------------------------------------
Total SKS : 9
IPK       : 3.50
====================================================

--- Data Mahasiswa (dari Database) ---
  Mahasiswa | NIM: 1142400 | Nama: Silvia | Prodi: Sistem Informasi

--- Data Dosen (dari Database) ---
  Dosen | NIDN: D001 | Nama: Chandro Pardede, S.Kom., M.Sc. | Fakultas: Fakultas Informatika

--- Data Mata Kuliah (dari Database) ---
  IS301 | Sistem Operasi | 3 SKS
  IS302 | Basis Data | 3 SKS
  IS303 | Pemrograman Berorientasi Objek | 3 SKS

Program selesai. Koneksi database ditutup.
```

---

## Cara Compile & Run

### Prasyarat
1. **JDK 8+** terinstall
2. **SQLite JDBC Driver** — file `sqlite-jdbc-3.49.1.0.jar` di folder `lib/`
   - Download: https://repo1.maven.org/maven2/org/xerial/sqlite-jdbc/3.49.1.0/sqlite-jdbc-3.49.1.0.jar

### Menggunakan Makefile

```bash
make compile    # Compile semua file Java
make run        # Compile + jalankan program
make clean      # Hapus hasil kompilasi
```

### Manual (tanpa Makefile)

**Windows (PowerShell/CMD):**
```powershell
mkdir bin
javac -d bin -cp "lib/*" src/academic/model/*.java src/academic/driver/*.java
java -cp "bin;lib/*" academic.driver.Driver1
```

**Linux/Mac:**
```bash
mkdir -p bin
javac -d bin -cp "lib/*" src/academic/model/*.java src/academic/driver/*.java
java -cp "bin:lib/*" academic.driver.Driver1
```

> **Catatan:** Database SQLite otomatis dibuat sebagai file `akademik.db` di folder project. Tidak perlu install server database apapun.
