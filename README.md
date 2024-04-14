![](cover.jpeg)

# 🧑‍🎤 perf-boy [WIP]

![latestVersion](https://img.shields.io/github/v/release/theapache64/perf-boy)
<a href="https://twitter.com/theapache64" target="_blank">
<img alt="Twitter: theapache64" src="https://img.shields.io/twitter/follow/theapache64.svg?style=social" />
</a>

> A tool that analyzes your method trace file and generates the analysis in a spreadsheet

### ✨ Installation

```bash
sudo npm install -g perf-boy 
```

### 📦 Usage

```bash
perf-boy before.trace after.trace
```

## 🚀 Demo

**Usage**
```bash                                                              ✘ INT
❯ ls
after.trace  before.trace
❯ perf-boy before.trace after.trace
perf-boy (0.0.2)
📖 Parsing traces... (this step may take a while)
🔍 Comparing traces...
📝 Writing to spreadsheet (before-vs-after.xlsx)...
📜 Creating sheet: All Threads
📝 Writing data to sheet: All Threads
📜 Creating sheet: Main Thread
📝 Writing data to sheet: Main Thread
📜 Creating sheet: Background Threads
📝 Writing data to sheet: Background Threads
🚀 Writing to file: before-vs-after.xlsx
Done ✅
❯ ls
after.trace before-vs-after.xlsx before.trace
```

**Output**

![image](https://github.com/theapache64/perf-boy/assets/9678279/a502962a-0e47-4dbb-b664-33a2ac169414)

## ✍️ Author

👤 **theapache64**

* Twitter: <a href="https://twitter.com/theapache64" target="_blank">@theapache64</a>
* Email: theapache64@gmail.com

Feel free to ping me 😉

## 🤝 Contributing

Contributions are what make the open source community such an amazing place to be learn, inspire, and create. Any
contributions you make are **greatly appreciated**.

1. Open an issue first to discuss what you would like to change.
1. Fork the Project
1. Create your feature branch (`git checkout -b feature/amazing-feature`)
1. Commit your changes (`git commit -m 'Add some amazing feature'`)
1. Push to the branch (`git push origin feature/amazing-feature`)
1. Open a pull request

Please make sure to update tests as appropriate.

## ❤ Show your support

Give a ⭐️ if this project helped you!

<a href="https://www.patreon.com/theapache64">
  <img alt="Patron Link" src="https://c5.patreon.com/external/logo/become_a_patron_button@2x.png" width="160"/>
</a>

<a href="https://www.buymeacoffee.com/theapache64" target="_blank">
    <img src="https://cdn.buymeacoffee.com/buttons/v2/default-yellow.png" alt="Buy Me A Coffee" width="160">
</a>

## ☑️ TODO

- [ ] Single trace file analysis

## 📝 License

```
Copyright © 2024 - theapache64

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

_This README was generated by [readgen](https://github.com/theapache64/readgen)_ ❤
