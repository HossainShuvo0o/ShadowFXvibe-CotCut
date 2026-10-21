import os

with open('public/assets/aistudio/Paste_October_21__2026_-_10_51AM_a64aa602', 'r', encoding='utf-8', errors='ignore') as f:
    text = f.read()

target = 'content:`package com.banglamotion.studio'
idx = text.find(target)
print('Target index:', idx)
if idx != -1:
    start = idx + len('content:`')
    # Find closing backtick, keeping in mind backticks might be escaped or inside strings
    # But usually in JS bundle it ends with `}` or similar
    end = text.find('`}', start)
    if end == -1:
        end = text.find('`', start)
    print('End index:', end)
    code = text[start:end]
    print('Code length:', len(code))
    print('Lines count:', len(code.splitlines()))
    with open('/tmp/extracted_MainActivity.kt', 'w', encoding='utf-8') as out:
        out.write(code)
    print('Saved to /tmp/extracted_MainActivity.kt successfully!')
else:
    print('Target not found, searching package com.banglamotion...')
    idx2 = text.find('package com.banglamotion.studio')
    print('Alternative index:', idx2)
