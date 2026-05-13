# Sistem Pengelolaan Nilai dan Rekam Jejak Akademik Mahasiswa

## Deskripsi

Program ini adalah aplikasi CLI (Command Line Interface) berbasis Java yang mensimulasikan proses pengelolaan data akademik — mahasiswa, dosen, mata kuliah, dan nilai — di jurusan Sistem Informasi. Data diproses terlebih dahulu di memori menggunakan Java Collection Framework, kemudian disimpan secara permanen ke database SQLite melalui JDBC. Program ini mengintegrasikan **15 konsep utama** Pemrograman Berorientasi Objek dalam satu alur eksekusi yang saling terhubung dan terstruktur.

---

## Bagaimana 15 Konsep Ini Bekerja Bersama?

Bayangkan alurnya seperti ini:

Pertama, dibutuhkan **model data yang terstruktur**. Di dunia kampus, mahasiswa dan dosen sama-sama punya nama dan identitas — maka dibuat satu cetakan umum bernama `CivitasAkademika` sebagai **Abstract Class [1]**. Class ini tidak bisa diinstansiasi langsung karena hanya mendefinisikan kerangka — method seperti `getPeran()` dibiarkan abstract dan wajib diisi oleh subclass. Inilah **Abstraction**.

Dari cetakan itu, `Mahasiswa` dan `Dosen` dibuat sebagai turunannya — inilah **Inheritance [2]**. Mahasiswa punya NIM dan Prodi, Dosen punya NIDN dan Fakultas, tapi keduanya berbagi atribut dasar `id`, `nama`, `unitKerja` dari parent. Ketika `getPeran()` dipanggil pada objek yang berbeda, hasilnya berbeda secara otomatis — inilah **Polymorphism [3]**. Semua field dibuat `private` dan hanya bisa diakses melalui getter/setter — inilah **Encapsulation [4]**.

Agar setiap class punya kontrak perilaku yang jelas dan terpisah, diterapkan **Interface [5]**: `Printable` untuk kontrak tampilan, `Validatable` untuk kontrak validasi, `Repository<T>` untuk kontrak CRUD, dan `Persistable` untuk kontrak penyimpanan. Nilai akademik tidak lagi disimpan sebagai String biasa yang rawan typo, melainkan menggunakan **Enumeration [6]** `HurufMutu` yang membawa bobot dan keterangan secara langsung. Hasil kalkulasi transkrip dikemas dalam **Record [7]** `RingkasanTranskrip` — immutable data carrier yang otomatis menghasilkan constructor, getter, `equals()`, dan `toString()`.

Operasi CRUD diabstraksi melalui **Generics [8]** `Repository<T>` sehingga reusable untuk semua entitas tanpa duplikasi kode. Di dalam setiap class model terdapat **Nested Class [9]** `Builder` yang memudahkan pembuatan objek dengan validasi otomatis saat `build()`. Di dalam method `tampilkanMenu()` terdapat **Local Class [10]** `MenuFormatter` yang hanya hidup dalam scope method tersebut — tidak bisa diakses dari luar. Untuk sorting dan tampilan sekali pakai tanpa perlu membuat class baru, digunakan **Anonymous Class [11]** sebagai `Comparator`, `Printable`, dan `Runnable`.

Saat user memilih menu `[5] Simpan ke Database`, semua data di buffer memori dikirim ke SQLite melalui **JDBC [12]** menggunakan `PreparedStatement` yang aman dari SQL injection. Data sementara dikelola menggunakan **JCF [13]** — `ArrayList` sebagai buffer dan `HashMap` untuk lookup cepat O(1). Pemetaan objek Java ke SQL dan sebaliknya dilakukan oleh **Custom ORM [14]** `AkademikORM` tanpa framework eksternal. Seluruh arsitektur dirancang mengikuti **SOLID Principle [15]**.

```
[User Input — Menu Interaktif atau Perintah Langsung]
          |
          v
  [Buat Objek Java]
    <- [1]  ABSTRACTION  : CivitasAkademika abstract class
    <- [2]  INHERITANCE  : Mahasiswa & Dosen extends CivitasAkademika
    <- [4]  ENCAPSULATION: field private, akses via getter/setter
    <- [6]  ENUMERATION  : HurufMutu, StatusAkademik
    <- [9]  NESTED CLASS : Builder pattern di setiap model
          |
          v
  [Validasi & Tampung di Memori]
    <- [5]  INTERFACE    : Validatable.validasi()
    <- [13] JCF          : ArrayList buffer + HashMap lookup O(1)
          |
          v  (menu [5] SIMPAN_KE_DB)
  [Objek ke SQL]
    <- [14] CUSTOM ORM   : AkademikORM Objek ke PreparedStatement
    <- [12] JDBC         : PreparedStatement ke SQLite
    <- [11] ANON CLASS   : Comparator sort sebelum INSERT
    <- [8]  GENERICS     : Repository<T>.save(entity)
          |
          v
  [Simpan Permanen ke Database akademik.db]

          ===== menu [6] CETAK_TRANSKRIP =====

  [Query Database]
    <- [12] JDBC         : SELECT JOIN nilai + mata_kuliah
    <- [14] CUSTOM ORM   : ResultSet ke List<Nilai>
    <- [13] JCF          : ArrayList<Nilai> hasil query
    <- [11] ANON CLASS   : Comparator sort by kode MK
          |
          v
  [Bangun Konten Transkrip ke List<String>]
    <- [7]  RECORD       : RingkasanTranskrip (IPK, predikat)
    <- [3]  POLYMORPHISM : cetak() via Printable interface
    <- [5]  INTERFACE    : Printable.getRingkasan()
          |
          v
  [Cetak ke Konsol + Tawaran Export ke .txt]
    <- [10] LOCAL CLASS  : MenuFormatter di tampilkanMenu()
    <- [15] SOLID        : SRP, OCP, LSP, ISP, DIP
```

---

## Tabel Pemetaan 15 Konsep

| No | Konsep | File Utama | Penjelasan |
|----|--------|------------|------------|
| 1 | **Abstraction** | `CivitasAkademika.java` | Abstract class dengan method abstract `getPeran()`, `getLabelId()`, `getUnitKerja_Label()` — tidak bisa diinstansiasi langsung. |
| 2 | **Inheritance** | `Mahasiswa.java`, `Dosen.java` | Keduanya `extends CivitasAkademika`, mewarisi `id`, `nama`, `unitKerja`, serta semua method parent. |
| 3 | **Polymorphism** | Semua model, `Driver1.java` | `getPeran()`, `cetak()`, `getRingkasan()` di-override di setiap subclass. Objek diperlakukan seragam via interface `Printable`. |
| 4 | **Encapsulation** | Semua model | Semua field `private`, akses hanya via getter/setter yang terdefinisi dengan jelas. |
| 5 | **Interface** | `Printable.java`, `Validatable.java`, `Repository.java`, `Persistable.java` | Empat interface terpisah mengikuti prinsip Interface Segregation (SOLID). |
| 6 | **Enumeration** | `HurufMutu.java`, `StatusAkademik.java` | `HurufMutu` membawa bobot dan keterangan per nilai. `StatusAkademik` membawa label status studi mahasiswa. |
| 7 | **Record** | `RingkasanTranskrip.java` | Immutable data carrier hasil kalkulasi transkrip: NIM, nama, prodi, totalSks, IPK, dan predikat kelulusan. |
| 8 | **Generics** | `Repository.java`, `*Repository.java` | `Repository<T>` reusable untuk semua entitas: `Repository<Mahasiswa>`, `Repository<Dosen>`, `Repository<MataKuliah>`, `Repository<Nilai>`. |
| 9 | **Nested Class** | `Mahasiswa.java`, `Dosen.java`, `MataKuliah.java` | Static inner class `Builder` di setiap model — membangun objek dengan validasi otomatis saat `build()` dipanggil. |
| 10 | **Local Class** | `Driver1.java` | Class `MenuFormatter` didefinisikan di dalam method `tampilkanMenu()` dan hanya hidup dalam scope method tersebut. |
| 11 | **Anonymous Class** | `Driver1.java` | `Comparator<Mahasiswa>` untuk sort buffer sebelum INSERT, `Printable` anonim untuk tampilkan info huruf mutu, `Runnable` untuk cetak garis pemisah. |
| 12 | **JDBC** | `DatabaseManager.java`, `AkademikORM.java` | Singleton connection ke SQLite via `DriverManager`, `PreparedStatement` untuk semua query, `PRAGMA foreign_keys = ON` untuk integritas data. |
| 13 | **JCF** | `Driver1.java` | `ArrayList` sebagai buffer data + menampung hasil query. `HashMap` untuk lookup NIM/kode MK di buffer dengan kompleksitas O(1). |
| 14 | **Custom ORM** | `AkademikORM.java` | Pemetaan manual Objek ke SQL dan sebaliknya: `insertMahasiswa()` = Objek ke INSERT, `selectNilaiByNim()` = JOIN ResultSet ke List tanpa framework seperti Hibernate. |
| 15 | **SOLID** | Seluruh arsitektur | **S**: satu tanggung jawab per class. **O**: Repository bisa di-extend tanpa ubah yang lama. **L**: subclass bisa gantikan parent. **I**: interface kecil terpisah. **D**: Driver bergantung pada `Repository<T>` bukan implementasi konkret. |

---

## Struktur Folder

```
12_ProyekPBO/
├── .github/
│   └── workflows/
│       └── classroom.yml              <- CI/CD GitHub Classroom
├── bin/
│   └── academic/                      <- hasil compile (.class)
├── lib/
│   └── sqlite-jdbc-3.49.1.0.jar      <- JDBC driver SQLite (jembatan ke database)
├── src/
│   └── academic/
│       ├── driver/
│       │   └── Driver1.java           <- entry point CLI          [10][11][13][3][8]
│       ├── model/
│       │   ├── CivitasAkademika.java  <- abstract parent class    [1][2][3][4][5]
│       │   ├── Mahasiswa.java         <- extends CivitasAkademika [2][3][6][9]
│       │   ├── Dosen.java             <- extends CivitasAkademika [2][3][9]
│       │   ├── MataKuliah.java        <- model mata kuliah        [4][5][9]
│       │   ├── Nilai.java             <- model nilai + enum       [4][5][6]
│       │   └── AkademikORM.java       <- Custom ORM               [12][13][14]
│       ├── enums/
│       │   ├── HurufMutu.java         <- enum nilai akademik      [6]
│       │   └── StatusAkademik.java    <- enum status mahasiswa    [6]
│       ├── interfaces/
│       │   ├── Printable.java         <- kontrak tampilan         [5]
│       │   ├── Validatable.java       <- kontrak validasi         [5]
│       │   ├── Repository.java        <- kontrak CRUD generic     [5][8]
│       │   └── Persistable.java       <- kontrak persistence      [5]
│       ├── record/
│       │   └── RingkasanTranskrip.java <- immutable data carrier  [7]
│       ├── repository/
│       │   ├── MahasiswaRepository.java  <- impl Repository<Mahasiswa>  [8][15]
│       │   ├── DosenRepository.java      <- impl Repository<Dosen>      [8][15]
│       │   ├── MataKuliahRepository.java <- impl Repository<MataKuliah> [8][15]
│       │   └── NilaiRepository.java      <- impl Repository<Nilai>      [8][15]
│       └── db/
│           └── DatabaseManager.java   <- Singleton JDBC connection [12][15]
├── transkrip/                         <- hasil export transkrip .txt (auto-generated)
├── akademik.db                        <- file database SQLite (auto-generated)
├── .classpath
├── .gitignore
├── .project
├── changelog.txt
├── Makefile
└── README.md
```

---

## Desain Class

### Hierarchy Inheritance

```
      +----------------------------------+
      |        CivitasAkademika         |  <- Abstract Class [1][2]
      |  implements Printable,           |
      |             Validatable          |  <- Interface [5]
      +----------------------------------+
      | - id        : String             |
      | - nama      : String             |  <- Encapsulation [4]
      | - unitKerja : String             |
      +----------------------------------+
      | + getPeran()     : abstract      |  <- Abstraction [1]
      | + getLabelId()   : abstract      |  <- Polymorphism [3]
      | + cetak()        : override      |
      | + getRingkasan() : override      |
      +---------------+------------------+
                      |
          +-----------+-----------+
          |                       |
+---------+------------+  +-------+------------------+
|      Mahasiswa        |  |          Dosen            |
| + static Builder      |  | + static Builder          |  <- Nested Class [9]
+-----------------------+  +---------------------------+
| - status:StatusAkademik|  |                          |  <- Enum [6]
| + getNim()             |  | + getNidn()              |
| + getProdi()           |  | + getFakultas()           |
| + getPeran()->"Mhs"    |  | + getPeran()->"Dosen"     |  <- Polymorphism [3]
+-----------------------+  +---------------------------+

+----------------------------------+
|           MataKuliah             |
|  implements Printable,Validatable|
|  + static Builder                |  <- Nested Class [9]
+----------------------------------+
| - kode : String                  |
| - nama : String                  |
| - sks  : int                     |
+----------------------------------+

+----------------------------------+
|              Nilai               |
|  implements Printable,Validatable|
+----------------------------------+
| - hurufMutu : HurufMutu (enum)   |  <- Enumeration [6]
| - namaMK    : String (dari JOIN) |
| - sks       : int    (dari JOIN) |
| + getBobot()->hurufMutu.getBobot()|
+----------------------------------+
```

### Generic Repository Pattern

```
      +-----------------------------+
      |       Repository<T>         |  <- Interface + Generics [5][8]
      +-----------------------------+
      | + save(T entity)            |
      | + findById(String id)       |
      | + findAll()                 |
      | + deleteById(String id)     |
      | + existsById(String id)     |
      +-----------------------------+
               |  implements
    +----------+----------+----------+
    |          |          |          |
Mahasiswa   Dosen    MataKuliah  NilaiRepository
Repository  Repository Repository  (+ findByNim()
                                    + getRingkasanTranskrip())
```

### Record RingkasanTranskrip

```
+--------------------------------------------+
|         RingkasanTranskrip (Record)        |  <- Record [7]
+--------------------------------------------+
| nim()       : String  (immutable)          |
| nama()      : String  (immutable)          |
| prodi()     : String  (immutable)          |
| totalSks()  : int     (immutable)          |
| ipk()       : double  (immutable)          |
+--------------------------------------------+
| + getIpkFormatted() : "3.50"               |
| + getPredikat()     : "Sangat Memuaskan"   |
+--------------------------------------------+
```

---

## Skema SQL (DDL)

Tabel-tabel ini otomatis dibuat oleh `DatabaseManager.initializeDatabase()` saat program pertama kali dijalankan. Database **tidak terhapus otomatis** saat program berjalan.

```sql
-- Tabel Mahasiswa (kolom status untuk enum StatusAkademik)
CREATE TABLE IF NOT EXISTS mahasiswa (
    nim     TEXT PRIMARY KEY,
    nama    TEXT NOT NULL,
    prodi   TEXT NOT NULL,
    status  TEXT NOT NULL DEFAULT 'AKTIF'
);

-- Tabel Dosen
CREATE TABLE IF NOT EXISTS dosen (
    nidn     TEXT PRIMARY KEY,
    nama     TEXT NOT NULL,
    fakultas TEXT NOT NULL
);

-- Tabel Mata Kuliah
CREATE TABLE IF NOT EXISTS mata_kuliah (
    kode TEXT PRIMARY KEY,
    nama TEXT NOT NULL,
    sks  INTEGER NOT NULL CHECK(sks > 0)
);

-- Tabel Nilai (relasi mahasiswa <-> mata kuliah)
CREATE TABLE IF NOT EXISTS nilai (
    id         INTEGER PRIMARY KEY AUTOINCREMENT,
    nim        TEXT NOT NULL,
    kode_mk    TEXT NOT NULL,
    huruf_mutu TEXT NOT NULL,
    FOREIGN KEY (nim)     REFERENCES mahasiswa(nim),
    FOREIGN KEY (kode_mk) REFERENCES mata_kuliah(kode),
    UNIQUE(nim, kode_mk)
);
```

---

## Konversi Huruf Mutu ke Bobot

Menggunakan **Enumeration `HurufMutu`** — bukan String biasa, membawa data dan behavior sendiri.

| Huruf Mutu | Bobot | Keterangan |
|------------|-------|------------|
| A  | 4.0 | Sangat Baik |
| AB | 3.5 | Antara Sangat Baik dan Baik |
| B  | 3.0 | Baik |
| BC | 2.5 | Antara Baik dan Cukup |
| C  | 2.0 | Cukup |
| D  | 1.0 | Kurang |
| E  | 0.0 | Tidak Lulus |

**Rumus IPK:** `IPK = Sigma(bobot x SKS) / Sigma(SKS)`

**Predikat IPK** — dari `RingkasanTranskrip` Record:

| Rentang IPK | Predikat |
|-------------|----------|
| 3.51 - 4.00 | Dengan Pujian (Cum Laude) |
| 3.01 - 3.50 | Sangat Memuaskan |
| 2.76 - 3.00 | Memuaskan |
| 2.00 - 2.75 | Cukup |
| < 2.00      | Kurang |

---

## Tampilan Menu Program

```
========================================================
  SISTEM PENGELOLAAN NILAI & REKAM JEJAK AKADEMIK
  Inheritance | JCF | JDBC | Custom ORM | SOLID
========================================================
  Masukkan nomor menu atau perintah langsung.
  Ketik '---' untuk mengakhiri program.

  MENU UTAMA
  ----------------------------------------
  [ Data Akademik ]
  ----------------------------------------
  [1]  Tambah Mahasiswa
  [2]  Tambah Dosen
  [3]  Tambah Mata Kuliah
  [4]  Input Nilai Mahasiswa
  ----------------------------------------
  [ Database & Laporan ]
  ----------------------------------------
  [5]  Simpan Semua ke Database
  [6]  Cetak Transkrip Nilai
  [7]  Lihat Semua Data
  [8]  Hapus Data (Mhs/Dosen/MK/Nilai)
  [9]  Hapus Seluruh Database
  ----------------------------------------
  [ Sistem ]
  ----------------------------------------
  [0]  Keluar  (atau ketik ---)
  ----------------------------------------

  Pilih menu  :
```

---

## Daftar Perintah CLI

Program mendukung **dua mode input** yang bisa digunakan bergantian dalam satu sesi:

### Mode 1 — Menu Interaktif
Ketik nomor menu, program akan memandu input satu per satu.

### Mode 2 — Perintah Langsung (format `#`)

| Perintah | Format | Keterangan |
|----------|--------|------------|
| Tambah Mahasiswa   | `TAMBAH_MAHASISWA#NIM#Nama#Prodi[#Status]`  | Status opsional: AKTIF/CUTI/LULUS/DO |
| Tambah Dosen       | `TAMBAH_DOSEN#NIDN#Nama#Fakultas`           | Simpan ke buffer memori |
| Tambah Mata Kuliah | `TAMBAH_MATAKULIAH#Kode#Nama#SKS`           | Simpan ke buffer memori |
| Input Nilai        | `INPUT_NILAI#NIM#KodeMK#HurufMutu`          | Huruf mutu: A/AB/B/BC/C/D/E |
| Simpan ke DB       | `SIMPAN_KE_DB`                              | Flush buffer ke database |
| Cetak Transkrip    | `CETAK_TRANSKRIP#NIM`                       | Tampilkan transkrip + IPK + predikat |
| Lihat Semua        | `LIHAT_SEMUA`                               | Tampilkan semua data dari database |
| Hapus Mahasiswa    | `HAPUS_MAHASISWA#NIM`                       | Hapus mahasiswa beserta seluruh nilainya |
| Hapus Dosen        | `HAPUS_DOSEN#NIDN`                          | Hapus dosen dari database |
| Hapus Mata Kuliah  | `HAPUS_MATAKULIAH#Kode`                     | Hapus MK beserta semua nilai terkait |
| Hapus Nilai        | `HAPUS_NILAI#NIM#KodeMK`                    | Hapus satu nilai spesifik |
| Hapus Database     | `HAPUS_DATABASE`                            | Hapus seluruh file database (butuh konfirmasi) |
| Keluar             | `---`                                       | Tutup koneksi dan keluar program |

---

## Contoh Simulasi Input dan Output

### Skenario 1 — Input Lengkap via Perintah Langsung

**Input:**
```
TAMBAH_MAHASISWA#1142400#Silvia#Sistem Informasi
TAMBAH_MAHASISWA#1142401#Budi#Teknik Informatika#AKTIF
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

**Output:**
```
========================================================
  SISTEM PENGELOLAAN NILAI & REKAM JEJAK AKADEMIK
  Inheritance | JCF | JDBC | Custom ORM | SOLID
========================================================
  Masukkan nomor menu atau perintah langsung.
  Ketik '---' untuk mengakhiri program.

[OK] Mahasiswa -> buffer: Silvia (NIM: 1142400) | Aktif
[OK] Mahasiswa -> buffer: Budi (NIM: 1142401) | Aktif
[OK] Dosen -> buffer: Chandro Pardede, S.Kom., M.Sc. (NIDN: D001)
[OK] MataKuliah -> buffer: Sistem Operasi (IS301, 3 SKS)
[OK] MataKuliah -> buffer: Basis Data (IS302, 3 SKS)
[OK] MataKuliah -> buffer: Pemrograman Berorientasi Objek (IS303, 3 SKS)
[OK] Nilai -> buffer: NIM 1142400 | MK IS301 | A (bobot: 4.0) - Sangat Baik
[OK] Nilai -> buffer: NIM 1142400 | MK IS302 | AB (bobot: 3.5) - Antara Sangat Baik dan Baik
[OK] Nilai -> buffer: NIM 1142400 | MK IS303 | B (bobot: 3.0) - Baik

--- Menyimpan data ke database ---
[OK] 9 record berhasil disimpan ke database.
[OK] Buffer memori dibersihkan.

====================================================
           TRANSKRIP NILAI AKADEMIK
           Sistem Informasi Akademik
====================================================
NIM      : 1142400
Nama     : Silvia
Prodi    : Sistem Informasi
Status   : Aktif
====================================================
Kode       Mata Kuliah                      SKS   Nilai  Bobot
----------------------------------------------------
IS301      Sistem Operasi                    3   A      4.0
IS302      Basis Data                        3   AB     3.5
IS303      Pemrograman Berorientasi Objek    3   B      3.0
----------------------------------------------------
Total SKS : 9
IPK       : 3.50
Predikat  : Sangat Memuaskan
====================================================
Dicetak pada : 10-05-2026 05:30:00
====================================================

  Export transkrip ke file .txt? (y/n): y
[OK] Transkrip berhasil diekspor ke: transkrip\transkrip_1142400_10052026_053000.txt

  ====================================================
           SELURUH DATA AKADEMIK
  ====================================================

  DATA MAHASISWA (2 record)
  --------------------------------------------------
  NIM          | Nama                         | Prodi                  | Status
  --------------------------------------------------
  1142400      | Silvia                       | Sistem Informasi       | Aktif
  1142401      | Budi                         | Teknik Informatika     | Aktif

  DATA DOSEN (1 record)
  --------------------------------------------------
  NIDN         | Nama                          | Fakultas
  --------------------------------------------------
  D001         | Chandro Pardede, S.Kom., M.Sc.| Fakultas Informatika

  DATA MATA KULIAH (3 record)
  --------------------------------------------------
  Kode     | Nama                                | SKS
  --------------------------------------------------
  IS301    | Sistem Operasi                      | 3 SKS
  IS302    | Basis Data                          | 3 SKS
  IS303    | Pemrograman Berorientasi Objek      | 3 SKS

  ====================================================
  Total: 2 Mahasiswa | 1 Dosen | 3 Mata Kuliah
  ====================================================

Program selesai. Sampai jumpa!
```

---

### Skenario 2 — Input via Menu Interaktif

**Menu [1] Tambah Mahasiswa:**
```
  Pilih menu  : 1

--- Tambah Mahasiswa ---
  NIM    : 1142402
  Nama   : Rina
  Prodi  : Sistem Informasi
  Status (AKTIF/CUTI/LULUS/DO) [Enter=AKTIF]:
[OK] Mahasiswa -> buffer: Rina (NIM: 1142402) | Aktif
```

**Menu [4] Input Nilai:**
```
  Pilih menu  : 4

--- Input Nilai Mahasiswa ---
  Huruf mutu yang tersedia:
    A   -> 4.0 (Sangat Baik)
    AB  -> 3.5 (Antara Sangat Baik dan Baik)
    B   -> 3.0 (Baik)
    BC  -> 2.5 (Antara Baik dan Cukup)
    C   -> 2.0 (Cukup)
    D   -> 1.0 (Kurang)
    E   -> 0.0 (Tidak Lulus)

  NIM Mahasiswa : 1142402
  Kode MK       : IS301
  Huruf Mutu    : A
[OK] Nilai -> buffer: NIM 1142402 | MK IS301 | A (bobot: 4.0) - Sangat Baik
```

**Menu [7] Lihat Semua (saat database kosong):**
```
  Pilih menu  : 7

  ====================================================
  [INFO] Database masih kosong.
  Gunakan menu [1]-[4] untuk menambah data,
  lalu pilih [5] Simpan ke Database terlebih dahulu.
  ====================================================
```

**Menu [8] Hapus Data:**
```
  Pilih menu  : 8

--- Hapus Data ---
  [1] Hapus Mahasiswa (termasuk nilai)
  [2] Hapus Dosen
  [3] Hapus Mata Kuliah (termasuk nilai)
  [4] Hapus Nilai Tertentu
  Pilih: 1
  NIM: 1142401
[OK] Mahasiswa NIM 1142401 dan nilainya berhasil dihapus.
```

**Menu [9] Hapus Database:**
```
  Pilih menu  : 9

[PERINGATAN] Aksi ini akan menghapus SELURUH database secara permanen!
Ketik 'HAPUS' untuk konfirmasi: HAPUS
[DB] Database 'akademik.db' berhasil dihapus.
[OK] Database dihapus. Membuat database baru...
[DB] Database siap. File: akademik.db
```

---

## Cara Compile & Run

### Prasyarat

1. **JDK 16+** terinstall (diperlukan untuk fitur Record Java 16+)
2. **SQLite JDBC Driver** — file `sqlite-jdbc-3.49.1.0.jar` sudah ada di folder `lib/`
3. **make** tersedia di terminal (Git Bash / MinGW / WSL untuk Windows)

### Menggunakan Makefile (Windows CMD)

```cmd
make compile    :: Compile semua file Java
make run        :: Compile lalu jalankan program
make clean      :: Hapus hasil kompilasi
```

Isi Makefile yang digunakan:

```makefile
compile:
	@if exist bin rmdir /s /q bin
	@mkdir bin
	@dir /s /b src\*.java > sources.txt
	javac -d bin -cp "lib\*" @sources.txt
	@del sources.txt

run: compile
	java -cp "bin;lib\*" academic.driver.Driver1

clean:
	@if exist bin rmdir /s /q bin
	@mkdir bin

.PHONY: compile run clean
```

### Manual tanpa Makefile (Windows CMD)

```cmd
if not exist bin mkdir bin
dir /s /b src\*.java > sources.txt
javac -d bin -cp "lib\*" @sources.txt
del sources.txt
java -cp "bin;lib\*" academic.driver.Driver1
```

### Manual tanpa Makefile (Linux / Mac)

```bash
mkdir -p bin
find src -name "*.java" > sources.txt
javac -d bin -cp "lib/*" @sources.txt
rm sources.txt
java -cp "bin:lib/*" academic.driver.Driver1
```

---

## Verifikasi Integrasi Database

Database `akademik.db` tersimpan sebagai file SQLite di folder proyek. Ada tiga cara untuk memverifikasi data benar-benar tersimpan:

### 1. SQLite CLI via Terminal

Download SQLite tools di `https://www.sqlite.org/download.html` (pilih sqlite-tools-win-x64), lalu:

```cmd
sqlite3 akademik.db
```

```sql
-- Lihat semua tabel
.tables

-- Lihat struktur tabel
.schema mahasiswa

-- Lihat semua data mahasiswa
SELECT * FROM mahasiswa;

-- Lihat transkrip via JOIN
SELECT m.nama, mk.nama, n.huruf_mutu
FROM nilai n
JOIN mahasiswa m ON n.nim = m.nim
JOIN mata_kuliah mk ON n.kode_mk = mk.kode;

-- Keluar
.quit
```

### 2. DB Browser for SQLite (GUI Visual)

Download di `https://sqlitebrowser.org/dl/` lalu buka file `akademik.db` langsung — tampil seperti spreadsheet, cocok untuk presentasi.

### 3. VS Code Extension

Install extension **SQLite Viewer** di VS Code, klik file `akademik.db` dari panel Explorer — data langsung tampil tanpa software tambahan.

---

## Catatan Penting

- **Database tidak terhapus otomatis** saat program dijalankan. Data bersifat persisten lintas sesi.
- Untuk menghapus database: gunakan menu `[9]` di dalam program, atau jalankan `del akademik.db` di CMD.
- File transkrip `.txt` tersimpan otomatis di folder `transkrip\` dengan nama `transkrip_NIM_ddMMyyyy_HHmmss.txt`.
- Jika muncul error `no such column: status`, hapus file `akademik.db` lama lalu jalankan ulang program — skema baru akan dibuat otomatis.
- Program mendukung **input campuran**: bisa pakai menu interaktif dan perintah langsung bergantian dalam satu sesi yang sama.