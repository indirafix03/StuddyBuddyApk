# StudyBuddy: AI-Powered Smart Task Management for Students

StudyBuddy adalah aplikasi asisten akademik berbasis Android (Mobile) yang dirancang untuk membantu mahasiswa mengelola aktivitas dan tugas perkuliahan secara lebih efektif, terstruktur, dan terorganisir. Aplikasi ini memungkinkan pengguna mencatat mata kuliah, menambahkan tugas beserta tenggat waktunya, memantau progres penyelesaian, serta mengelola jadwal akademik dalam satu platform yang mudah digunakan.

Keunggulan utama StudyBuddy terletak pada integrasi **Google Gemini API** sebagai *AI Productivity Assistant*. Dengan memanfaatkan kecerdasan buatan, aplikasi tidak hanya berfungsi sebagai tempat menyimpan daftar tugas, tetapi juga mampu memecah tugas besar menjadi langkah-langkah kecil (checklist) secara otomatis berdasarkan judul dan deskripsi tugas.

---

## 🎯 Tujuan Aplikasi

* **Organisasi Efektif**: Membantu mahasiswa mengelola tugas akademik secara lebih terstruktur dalam satu platform.
* **Progres Terarah**: Mengurangi risiko lupa terhadap detail tahapan pengerjaan tugas yang kompleks.
* **Peningkatan Produktivitas**: Memanfaatkan teknologi *Artificial Intelligence* untuk mengotomatisasi perencanaan tugas.
* **Smart User Experience**: Memberikan pengalaman manajemen tugas yang lebih cerdas melalui pembuatan checklist otomatis yang relevan.

---

## ✨ Fitur Utama & Inovasi AI

* **Manajemen Tugas & Mata Kuliah**: Menambah, mengedit, dan menghapus tugas kuliah lengkap dengan deadline, deskripsi, dan pengelompokkan berdasarkan mata kuliah yang sedang diambil.
* **AI Auto Checklist Generator (Google Gemini API)**: Fitur unggulan yang menganalisis konteks tugas secara otomatis untuk menghasilkan langkah pengerjaan yang relevan.
    > **Contoh Kasus:**
    > * **Input User (Judul):** *Membuat Makalah Artificial Intelligence*
    > * **Input User (Deskripsi):** *Makalah mengenai sejarah, perkembangan, dan penerapan AI di dunia pendidikan.*
    > * **Output AI (Checklist Otomatis):**
    >   - [ ] Mencari referensi jurnal
    >   - [ ] Menyusun outline makalah
    >   - [ ] Menulis pendahuluan dan sejarah AI
    >   - [ ] Membahas penerapan AI di pendidikan
    >   - [ ] Menyusun kesimpulan & memeriksa format penulisan
* **Agenda, Beranda & Kalender**: Tampilan ringkas untuk memantau tugas harian mendatang, jadwal kuliah, serta monitoring progres penyelesaian tugas.
* **Sistem Autentikasi**: Fitur registrasi dan login menggunakan sesi lokal untuk menjaga data tugas tetap personal.

---

## 🚀 Cara Penggunaan

1. **Registrasi & Login**: Buat akun baru atau masuk menggunakan akun yang sudah ada.
2. **Tambah Mata Kuliah**: Masukkan daftar mata kuliah yang sedang Anda tempuh melalui menu Tambah Mata Kuliah.
3. **Tambah Tugas**:
    - Pilih mata kuliah terkait.
    - Masukkan judul dan deskripsi tugas.
    - Klik tombol **"Generate task checklist"** untuk membiarkan AI menyusun langkah-langkah pengerjaan untuk Anda.
    - Simpan tugas.
4. **Pantau Progress**: Buka halaman Detail Tugas dan centang (*check*) setiap tahapan/langkah pengerjaan yang sudah selesai Anda lakukan.

---

## 🛠️ Implementasi Teknis

Aplikasi ini dibangun menggunakan arsitektur yang rapi dan teknologi berikut:

* **Bahasa Pemrograman**: Java
* **Penyimpanan Data Lokal**: SQLite (melalui `DatabaseHelper`) untuk menyimpan data pengguna, mata kuliah, tugas, dan sub-task checklist secara persisten.
* **Networking**: [Retrofit 2](https://square.github.io/retrofit/) digunakan untuk berkomunikasi secara asinkronus dengan API eksternal.
* **Kecerdasan Buatan**: Integrasi **Google Gemini API** menggunakan pemanggilan HTTP via Retrofit untuk fitur analisis tugas.
* **UI/UX Components**: 
    - Material Design Components untuk antarmuka yang modern dan bersih.
    - `SwipeRefreshLayout` untuk interaksi pemuatan AI yang responsif.
    - `SharedPreferences` untuk manajemen sesi login sederhana (*Keep Me Logged In*).
* **Arsitektur Kode**: Mengikuti pola struktur *package* yang modular (`Activities`, `Models`, `Adapters`, `Database`, `Network`, `Utils`).

---

## 📋 Persyaratan Sistem

* Android SDK v24 (Android 7.0 Nougat) atau lebih tinggi.
* Koneksi Internet aktif (diperlukan untuk mengirim request ke Gemini API).
* API Key Google Gemini (yang dikonfigurasi pada kelas `RetrofitClient` atau file `local.properties`).

---
Dibuat oleh **[Indira Ramayani]** sebagai bagian dari proyek inovasi manajemen tugas akademik.
