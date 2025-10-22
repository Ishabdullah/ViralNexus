#!/data/data/com.termux/files/usr/bin/bash

# ViralNexus Quick Push Script
# Makes it easy to commit and push changes

echo "🎮 ViralNexus Quick Push"
echo "========================"
echo ""

# Check if we're in the right directory
if [ ! -f "build.gradle" ]; then
    echo "❌ Error: Not in ViralNexus directory"
    echo "Run this from: ~/ViralNexus"
    exit 1
fi

# Show current status
echo "📋 Current changes:"
git status --short
echo ""

# Ask for commit message
echo "💬 Enter commit message (or press Ctrl+C to cancel):"
read -p "> " commit_msg

if [ -z "$commit_msg" ]; then
    echo "❌ Commit message cannot be empty"
    exit 1
fi

# Stage all changes
echo ""
echo "📦 Staging changes..."
git add .

# Commit
echo "💾 Creating commit..."
git commit -m "$commit_msg"

if [ $? -ne 0 ]; then
    echo "❌ Commit failed"
    exit 1
fi

# Push
echo "🚀 Pushing to GitHub..."
git push origin master

if [ $? -eq 0 ]; then
    echo ""
    echo "✅ Successfully pushed to GitHub!"
    echo "🏗️  GitHub Actions is now building your APK..."
    echo ""
    echo "📥 To download the built APK:"
    echo "   1. Go to: https://github.com/$(git remote get-url origin | sed 's/.*github.com[:/]\(.*\)\.git/\1/')/actions"
    echo "   2. Click on the latest workflow run"
    echo "   3. Download the 'debug-apk' artifact"
    echo ""
else
    echo "❌ Push failed. Check your GitHub authentication."
    echo "Run: gh auth status"
fi
