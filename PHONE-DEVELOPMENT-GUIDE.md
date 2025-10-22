# ViralNexus - Complete Phone Development Guide

## ✅ What We've Set Up

### 1. Development Environment (Complete!)
- ✅ **Java/JDK 21** - Installed and configured
- ✅ **Gradle 8.13** - Working via wrapper
- ✅ **Android SDK** - Platforms 34 & 35, Build Tools 33.0.1 & 34.0.0
- ✅ **Environment Variables** - Added to `~/.bashrc`:
  ```bash
  export ANDROID_HOME=$HOME/android-sdk
  export ANDROID_SDK_ROOT=$ANDROID_HOME
  export PATH=$ANDROID_HOME/cmdline-tools/latest/bin:$PATH
  export PATH=$ANDROID_HOME/platform-tools:$PATH
  export PATH=$ANDROID_HOME/build-tools/34.0.0:$PATH
  ```
- ✅ **Project Location** - `~/ViralNexus` (on executable filesystem)

### 2. GitHub Actions CI/CD (Complete!)
- ✅ **Workflow File** - `.github/workflows/build-apk.yml`
- ✅ **Auto-builds** - Triggers on push to master/main/develop
- ✅ **Manual builds** - Can trigger from GitHub Actions tab
- ✅ **Artifacts** - Debug and Release APKs uploaded automatically

### 3. Git Repository (Ready to Push!)
- ✅ **Initialized** - Git repo with initial commit
- ✅ **Files Organized** - `.gitignore` configured properly
- ✅ **Documentation** - README.md and project docs included

## 🔧 What You Need to Do Now

### Step 1: Get a New GitHub Token
Your current GitHub token has expired. Get a new one:

1. Go to: https://github.com/settings/tokens
2. Click **"Generate new token (classic)"**
3. Give it a name: "ViralNexus Phone Development"
4. Select scopes:
   - ✅ `repo` (all repo permissions)
   - ✅ `workflow` (for GitHub Actions)
5. Click **"Generate token"**
6. **COPY THE TOKEN** (you won't see it again!)

### Step 2: Update Token in Termux
```bash
# Edit .bashrc
nano ~/.bashrc

# Replace the old GITHUB_TOKEN line with your new token:
export GITHUB_TOKEN="YOUR_NEW_TOKEN_HERE"

# Save and reload
source ~/.bashrc
```

### Step 3: Create GitHub Repository
```bash
cd ~/ViralNexus

# Create repo and push
gh repo create ViralNexus --public --source=. --description="Pandemic simulation mobile game for Android" --push
```

## 📱 Your Development Workflow

### Editing Code on Your Phone

**Option 1: Using Termux editors**
```bash
cd ~/ViralNexus

# Edit with nano (beginner-friendly)
nano app/src/main/java/com/viralnexus/game/MainActivity.kt

# Or use vim (more powerful)
vim app/src/main/java/com/viralnexus/game/MainActivity.kt
```

**Option 2: Using Acode or other Android code editors**
1. Install Acode from Play Store (free, excellent Kotlin support)
2. Open folder: `/data/data/com.termux/files/home/ViralNexus`
3. Edit files with syntax highlighting and autocomplete

### Committing and Pushing Changes
```bash
cd ~/ViralNexus

# Check what changed
git status
git diff

# Stage changes
git add .

# Commit with descriptive message
git commit -m "Add player upgrade system"

# Push to GitHub (triggers automatic build!)
git push origin master
```

### Getting Your Built APK

After pushing:
1. Go to https://github.com/YOUR_USERNAME/ViralNexus
2. Click **Actions** tab at the top
3. Click on the most recent workflow run
4. Scroll down to **Artifacts**
5. Download **debug-apk** or **release-apk**
6. The zip file contains your APK!

**Installing on Your Phone:**
```bash
# If you downloaded to Downloads folder
cd ~/storage/downloads

# Unzip the artifact
unzip debug-apk.zip

# Install the APK
adb install -r app-debug.apk
# OR just open it in your file manager and tap to install
```

## 🎯 What You CAN Do on Your Phone

✅ **Edit all source code** - Kotlin, Java files
✅ **Modify resources** - XML layouts, strings, colors
✅ **Update build configs** - build.gradle, dependencies
✅ **Version control** - Git commits, branches, pull requests
✅ **Code reviews** - Review diffs, write issues
✅ **Documentation** - Update README, add comments
✅ **Install and test APKs** - Download from GitHub Actions

## ⚠️ What Your Phone CANNOT Do

❌ **Compile APKs locally** - AAPT2 is x86-64 only (not ARM64)
❌ **Run Android Emulator** - Requires nested virtualization
❌ **Debug with Android Studio** - Too resource-intensive

**Solution:** GitHub Actions handles all building in the cloud! ☁️

## 📚 Quick Reference

### Common Git Commands
```bash
# See what changed
git status

# View differences
git diff

# Create a branch
git checkout -b feature-name

# Switch branches
git checkout master

# Merge branch
git merge feature-name

# Push branch
git push origin feature-name

# Pull latest changes
git pull origin master
```

### Common Gradle Commands (These work!)
```bash
cd ~/ViralNexus

# List all tasks
./gradlew tasks

# Check dependencies
./gradlew dependencies

# Lint code
./gradlew lintDebug

# Run tests (unit tests that don't need AAPT2)
./gradlew testDebugUnitTest
```

### Project Structure
```
~/ViralNexus/
├── app/src/main/java/com/viralnexus/game/
│   ├── MainActivity.kt         # App entry point
│   ├── GameActivity.kt         # Main game screen
│   ├── engine/
│   │   ├── GameEngine.kt       # Core game logic
│   │   ├── WorldMap.kt         # 3D rendering
│   │   └── Earth3D.kt          # Globe model
│   └── ui/theme/               # Material3 theming
├── app/src/main/res/           # Resources
├── .github/workflows/          # CI/CD automation
└── ViralNexus-Project.md       # Development roadmap
```

## 🚀 Next Steps: Start Coding!

1. **Set up your GitHub token** (see Step 1-3 above)
2. **Make your first code change**
3. **Commit and push**
4. **Download your first automated build!**

You're now ready to develop Android apps entirely from your Samsung S24 Ultra! 📱✨

## 💡 Tips for Success

1. **Commit often** - Small, focused commits are easier to manage
2. **Descriptive messages** - "Fix bug" is bad, "Fix crash when rotating device" is good
3. **Test on device** - Download and install APKs frequently to test
4. **Use branches** - Keep master stable, experiment in branches
5. **Read build logs** - If GitHub Actions fails, click on the red X to see why

## 🆘 Troubleshooting

**Q: GitHub Actions build failed?**
A: Click on the failed workflow → Click on the "build" job → Read error messages

**Q: Can't push to GitHub?**
A: Check `gh auth status` and regenerate token if expired

**Q: Want to run the app immediately?**
A: You can't build locally, but you can trigger GitHub Actions manually via the Actions tab

**Q: Gradle commands hanging?**
A: Some commands need internet. Check connection and try again.

---

**You did it!** You have a complete Android development environment on your phone. 🎉
