import os

with open('public/assets/aistudio/Paste_October_21__2026_-_10_51AM_a64aa602', 'r', encoding='utf-8', errors='ignore') as f:
    text = f.read()

target = 'content:`package com.banglamotion.studio'
idx = text.find(target)
print('Target index:', idx)
if idx != -1:
    start = idx + len('content:`')
    end = text.find('`}', start)
    if end == -1:
        end = text.find('`', start)
    print('End index:', end)
    code = text[start:end]
    print('Code length:', len(code))
    print('Lines count:', len(code.splitlines()))
    with open('extracted_MainActivity.kt', 'w', encoding='utf-8') as out:
        out.write(code)
    print('Saved to extracted_MainActivity.kt successfully!')
