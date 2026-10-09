#!/bin/sh
# Проверяет исходники двух наборов папок, ничего не перезаписывает.
set -eu
repo_dir=$(CDPATH= cd -- "$(dirname -- "$0")/.." && pwd)
for n in 1 2 3 4 5; do
    diff -qr -x .DS_Store "$repo_dir/DriveNextLab$n/app/src" "$repo_dir/../DriveNextLab$n/app/src"
    diff -q "$repo_dir/DriveNextLab$n/app/build.gradle.kts" "$repo_dir/../DriveNextLab$n/app/build.gradle.kts"
    diff -q "$repo_dir/DriveNextLab$n/build.gradle.kts" "$repo_dir/../DriveNextLab$n/build.gradle.kts"
    if [ -d "$repo_dir/DriveNextLab$n/supabase" ]; then
        diff -qr "$repo_dir/DriveNextLab$n/supabase" "$repo_dir/../DriveNextLab$n/supabase"
    fi
    diff -q "$repo_dir/DriveNextLab$n/README.md" "$repo_dir/../DriveNextLab$n/README.md"
done
echo 'Все пять копий исходников совпадают.'
